package uk.gov.justice.digital.hmpps.oauth2server.resource.account

import com.microsoft.applicationinsights.TelemetryClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import uk.gov.justice.digital.hmpps.oauth2server.security.UserService
import uk.gov.justice.digital.hmpps.oauth2server.verify.VerifyMobileService
import uk.gov.justice.digital.hmpps.oauth2server.verify.VerifyMobileService.VerifyMobileException
import uk.gov.service.notify.NotificationClientException

@Controller
class ChangeMobileController(private val userService: UserService,
                             private val verifyMobileService: VerifyMobileService,
                             private val telemetryClient: TelemetryClient,
                             @Value("\${application.smoketest.enabled}") private val smokeTestEnabled: Boolean) {

  @GetMapping("/change-mobile")
  fun changeMobileRequest(authentication: Authentication): ModelAndView {
    val currentMobile = userService.getUserWithContacts(authentication.name).mobile
    return ModelAndView("account/changeMobile", "mobile", currentMobile)
  }

  @PostMapping("/change-mobile")
  fun changeMobile(@RequestParam mobile: String?, authentication: Authentication): ModelAndView {
    val username = authentication.name
    if (userService.isSameAsCurrentVerifiedMobile(username, mobile)) {
      return ModelAndView("redirect:/verify-mobile-already")
    }
    return try {
      val verifyCode = verifyMobileService.changeMobileAndRequestVerification(username, mobile)
      redirectToVerifyMobileWithVerifyCode(verifyCode)
    } catch (e: VerifyMobileException) {
      log.info("Validation failed for mobile phone number due to {}", e.reason)
      telemetryClient.trackEvent("VerifyMobileRequestFailure", mapOf("username" to username, "reason" to e.reason), null)

      createChangeOrVerifyMobileError(e.reason, mobile)
    } catch (e: NotificationClientException) {
      log.error("Failed to send sms due to", e)
      telemetryClient.trackEvent("VerifyMobileRequestFailure", mapOf("username" to username, "reason" to "notify"), null)

      createChangeOrVerifyMobileError("other", mobile)
    }
  }

  private fun createChangeOrVerifyMobileError(reason: String, currentMobile: String?): ModelAndView =
      ModelAndView("account/changeMobile")
          .addObject("error", reason)
          .addObject("mobile", currentMobile)

  private fun redirectToVerifyMobileWithVerifyCode(verifyCode: String): ModelAndView {
    val modelAndView = ModelAndView("redirect:/verify-mobile")
    if (smokeTestEnabled) {
      modelAndView.addObject("verifyCode", verifyCode)
    }
    return modelAndView
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}

