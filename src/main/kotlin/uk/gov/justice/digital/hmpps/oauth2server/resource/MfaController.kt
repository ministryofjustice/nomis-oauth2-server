@file:Suppress("SpringJavaInjectionPointsAutowiringInspection", "SpringMVCViewInspection")

package uk.gov.justice.digital.hmpps.oauth2server.resource

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.User.MfaPreferenceType
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.UserToken.TokenType
import uk.gov.justice.digital.hmpps.oauth2server.security.JwtAuthenticationSuccessHandler
import uk.gov.justice.digital.hmpps.oauth2server.security.MfaPassedAuthenticationToken
import uk.gov.justice.digital.hmpps.oauth2server.security.UserService
import uk.gov.justice.digital.hmpps.oauth2server.service.MfaService
import uk.gov.justice.digital.hmpps.oauth2server.verify.TokenService
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@Validated
class MfaController(
  private val jwtAuthenticationSuccessHandler: JwtAuthenticationSuccessHandler,
  private val tokenService: TokenService,
  private val userService: UserService,
  private val telemetryClient: TelemetryClient,
  private val mfaService: MfaService,
  @Value("\${application.smoketest.enabled}") private val smokeTestEnabled: Boolean,
) : AbstractMfaController(
  tokenService,
  telemetryClient,
  mfaService,
  smokeTestEnabled,
  "",
  "/login",
  "/mfa-challenge"
) {
  @GetMapping("/mfa-challenge")
  fun mfaChallengeRequest(
    @RequestParam(required = false) token: String?,
    @RequestParam mfaPreference: MfaPreferenceType,
  ): ModelAndView {

    if (token.isNullOrBlank()) return ModelAndView("redirect:/login?error=mfainvalid")

    val optionalError = tokenService.checkToken(TokenType.MFA, token)

    return optionalError.map { ModelAndView("redirect:/login?error=mfa$it") }
      .orElseGet {
        val codeDestination = mfaService.getCodeDestination(token, mfaPreference)
        ModelAndView("mfaChallenge", "token", token)
          .addObject("mfaPreference", mfaPreference)
          .addObject("codeDestination", codeDestination)
      }
  }

  @PostMapping("/mfa-challenge")
  @Throws(IOException::class, ServletException::class)
  fun mfaChallenge(
    @RequestParam token: String,
    @RequestParam mfaPreference: MfaPreferenceType,
    @RequestParam code: String,
    request: HttpServletRequest,
    response: HttpServletResponse,
  ): ModelAndView? = mfaChallenge(token, mfaPreference, code) {
    // now load the user
    val userPersonDetails = userService.findMasterUserPersonDetails(it).orElseThrow()

    val successToken = MfaPassedAuthenticationToken(userPersonDetails, "code", userPersonDetails.authorities)
    jwtAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, successToken)

    // return here is not required, since the success handler will have redirected
    null
  }

  @GetMapping("/mfa-resend")
  fun mfaResendRequest(@RequestParam token: String, @RequestParam mfaPreference: MfaPreferenceType): ModelAndView =
    createMfaResendRequest(token, mfaPreference)

  @PostMapping("/mfa-resend")
  fun mfaResend(
    @RequestParam token: String,
    @RequestParam mfaResendPreference: MfaPreferenceType,
  ): ModelAndView = createMfaResend(token, mfaResendPreference)
}
