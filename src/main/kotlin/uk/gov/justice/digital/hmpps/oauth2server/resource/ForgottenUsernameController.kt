package uk.gov.justice.digital.hmpps.oauth2server.resource

import com.microsoft.applicationinsights.TelemetryClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.User
import uk.gov.justice.digital.hmpps.oauth2server.service.ForgottenUsernameService
import uk.gov.justice.digital.hmpps.oauth2server.service.NotificationClientRuntimeException
import uk.gov.justice.digital.hmpps.oauth2server.verify.VerifyEmailService
import uk.gov.justice.digital.hmpps.oauth2server.verify.VerifyEmailService.ValidEmailException
import javax.servlet.http.HttpServletRequest

@Controller
@Validated
class ForgottenUsernameController(
  private val forgottenUsernameService: ForgottenUsernameService,
  private val verifyEmailService: VerifyEmailService,
  private val telemetryClient: TelemetryClient,
  @param:Value("\${application.smoketest.enabled}") private val smokeTestEnabled: Boolean,
) {

  @GetMapping("/forgotten-username")
  fun forgottenUsernameRequest(): String = "forgottenUsername"

  @PostMapping("/forgotten-username")
  fun forgottenUsername(
    @RequestParam(required = false) email: String?,
    request: HttpServletRequest,
  ): ModelAndView {
    if (email.isNullOrBlank()) {
      telemetryClient.trackEvent("AuthForgotUsernameRequestFailure", mapOf("error" to "missing email"), null)
      return ModelAndView("forgottenUsername", "error", "missing")
    }
    if (!email.contains("@")) {
      telemetryClient.trackEvent("AuthForgotUsernameRequestFailure", mapOf("error" to "not email"), null)
      return ModelAndView("forgottenUsername", "error", "notEmail")
    }

    return try {
      verifyEmailService.validateEmailAddress(email, User.EmailType.PRIMARY)
      val username = forgottenUsernameService.forgottenUsername(email, request.requestURL.toString())
      val modelAndView = ModelAndView("forgottenUsernameEmailSent")
      if (username.isNotEmpty()) {
        log.info("Forgotten username request success for {}", email)
        telemetryClient.trackEvent("AuthForgottenUsernameRequestSuccess", mapOf("email" to email), null)
        if (smokeTestEnabled) {
          modelAndView.addObject("usernames", username)
        }
      } else {
        log.info("Forgotten username request failed for {}", email)
        telemetryClient.trackEvent(
          "AuthForgottenUsernameRequestFailure",
          mapOf("email" to email, "error" to "no usernames found"),
          null
        )
        if (smokeTestEnabled) {
          modelAndView.addObject("usernamesMissing", true)
        }
      }
      modelAndView
    } catch (e: ValidEmailException) {
      telemetryClient.trackEvent(
        "AuthForgottenUsernameRequestFailure",
        mapOf("email" to email, "error" to "notValidPrimaryEmail"),
        null
      )
      ModelAndView("forgottenUsername", "error", "notValid")
    } catch (e: NotificationClientRuntimeException) {
      log.error("Failed to send forgotten username email due to", e)
      telemetryClient.trackEvent(
        "AuthForgottenUsernameRequestFailure",
        mapOf("email" to email, "error" to e.javaClass.simpleName),
        null
      )
      ModelAndView("forgottenUsername", "error", "other")
    }
  }

  companion object {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
