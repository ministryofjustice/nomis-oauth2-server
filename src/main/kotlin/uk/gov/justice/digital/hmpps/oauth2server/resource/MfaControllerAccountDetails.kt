@file:Suppress("SpringJavaInjectionPointsAutowiringInspection", "SpringMVCViewInspection")

package uk.gov.justice.digital.hmpps.oauth2server.resource

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.User.MfaPreferenceType
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.UserToken.TokenType
import uk.gov.justice.digital.hmpps.oauth2server.security.LockingAuthenticationProvider.MfaUnavailableException
import uk.gov.justice.digital.hmpps.oauth2server.service.LoginFlowException
import uk.gov.justice.digital.hmpps.oauth2server.service.MfaFlowException
import uk.gov.justice.digital.hmpps.oauth2server.service.MfaService
import uk.gov.justice.digital.hmpps.oauth2server.verify.TokenService
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@Validated
class MfaControllerAccountDetails(
  private val tokenService: TokenService,
  private val telemetryClient: TelemetryClient,
  private val mfaService: MfaService,
  @Value("\${application.smoketest.enabled}") private val smokeTestEnabled: Boolean,
) : AbstractMfaController(
  tokenService,
  "AccountDetails",
  "/account-details",
) {
  @GetMapping("/account/mfa-challenge")
  fun mfaChallengeRequestAccountDetail(
    authentication: Authentication,
    @RequestParam contactType: String,
    @RequestParam error: String?,
    @RequestParam token: String?,
    @RequestParam passToken: String?,
    @RequestParam mfaPreference: MfaPreferenceType?,
  ): ModelAndView {
    if (contactType == "email") {
      if (passToken.isNullOrEmpty()) return ModelAndView("redirect:/account-details?error=tokeninvalid")
      val optionalErrorForToken = tokenService.checkToken(TokenType.CHANGE, passToken)
      if (optionalErrorForToken.isPresent) return ModelAndView("redirect:/account-details?error=token${optionalErrorForToken.get()}")
    }
    try {
      if (error.isNullOrEmpty()) {
        // issue token to current mfa preference
        val mfaData = mfaService.createTokenAndSendMfaCode(authentication.name)
        val codeDestination = mfaService.getCodeDestination(mfaData.token, mfaData.mfaType)
        val modelAndView = ModelAndView("mfaChallengeAccountDetails", "token", mfaData.token)
          .addObject("mfaPreference", mfaData.mfaType)
          .addObject("codeDestination", codeDestination)
          .addObject("contactType", contactType)
          .addObject("passToken", passToken)
        if (smokeTestEnabled) modelAndView.addObject("smokeCode", mfaData.code)
        return modelAndView
      }
      val codeDestination = mfaService.getCodeDestination(token!!, mfaPreference!!)
      return ModelAndView("mfaChallengeAccountDetails", "token", token)
        .addObject("mfaPreference", mfaPreference)
        .addObject("codeDestination", codeDestination)
        .addObject("contactType", contactType)
        .addObject("passToken", passToken)
        .addObject("error", error)
    } catch (e: MfaUnavailableException) {
      return ModelAndView("redirect:/account-details?error=mfaunavailable")
    }
  }

  @PostMapping("/account/mfa-challenge")
  @Throws(IOException::class, ServletException::class)
  fun mfaChallengeAccountDetail(
    @RequestParam token: String,
    @RequestParam passToken: String,
    @RequestParam mfaPreference: MfaPreferenceType,
    @RequestParam code: String,
    @RequestParam contactType: String,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ): ModelAndView? {
    val optionalErrorForToken = tokenService.checkToken(TokenType.MFA, token)
    if (optionalErrorForToken.isPresent) {
      return ModelAndView("redirect:/account-details?error=mfa${optionalErrorForToken.get()}")
    }
    // can just grab token here as validated above
    val username = tokenService.getToken(TokenType.MFA, token).map { it.user.username }.orElseThrow()

    try {
      mfaService.validateAndRemoveMfaCode(token, code)
    } catch (e: MfaFlowException) {
      return ModelAndView("redirect:/account/mfa-challenge?contactType=$contactType")
        .addObject("token", token)
        .addObject("passToken", passToken)
        .addObject("error", e.error)
        .addObject("mfaPreference", mfaPreference)
    } catch (e: LoginFlowException) {
      return ModelAndView("redirect:/login?error=${e.error}")
    }

    // success, so forward on
    telemetryClient.trackEvent("MFAAuthenticateSuccess", mapOf("username" to username), null)

    return continueToChangeAccountDetails(username, contactType)
  }

  private fun continueToChangeAccountDetails(username: String, contactType: String): ModelAndView {
    // successfully passed 2fa, so generate change password token
    val token = tokenService.createToken(TokenType.ACCOUNT, username)

    @Suppress("SpringMVCViewInspection")
    return ModelAndView("redirect:/new-$contactType", "token", token)
  }

  @GetMapping("/account/mfa-resend")
  fun mfaResendRequest(
    @RequestParam contactType: String,
    @RequestParam token: String,
    @RequestParam passToken: String,
    @RequestParam mfaPreference: MfaPreferenceType
  ): ModelAndView = createMfaResendRequest(token, mfaPreference, extraModel(contactType, passToken))

  @PostMapping("/account/mfa-resend")
  @Throws(IOException::class, ServletException::class)
  fun mfaResend(
    @RequestParam contactType: String,
    @RequestParam token: String,
    @RequestParam passToken: String,
    @RequestParam mfaResendPreference: MfaPreferenceType,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ): ModelAndView {
    val optionalErrorForToken = tokenService.checkToken(TokenType.MFA, token)
    if (optionalErrorForToken.isPresent) {
      return ModelAndView("redirect:/account-detail?error=mfa${optionalErrorForToken.get()}")
    }

    val code = mfaService.resendMfaCode(token, mfaResendPreference)
    // shouldn't really get a code without a valid token, but cope with the scenario anyway
    if (code.isNullOrEmpty()) {
      return ModelAndView("redirect:/account-detail?error=mfainvalid")
    }

    val modelAndView = ModelAndView("redirect:/account/mfa-challenge", "token", token)
      .addObject("contactType", contactType)
      .addObject("mfaPreference", mfaResendPreference)
      .addObject("passToken", passToken)
    if (smokeTestEnabled) modelAndView.addObject("smokeCode", code)
    return modelAndView
  }

  private fun extraModel(contactType: String, passToken: String) =
    mapOf("contactType" to contactType, "passToken" to passToken)
}
