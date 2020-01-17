package uk.gov.justice.digital.hmpps.oauth2server.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.util.matcher.IpAddressMatcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.User
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.UserToken.TokenType
import uk.gov.justice.digital.hmpps.oauth2server.security.UserRetriesService
import uk.gov.justice.digital.hmpps.oauth2server.security.UserService
import uk.gov.justice.digital.hmpps.oauth2server.utils.IpAddressHelper
import uk.gov.justice.digital.hmpps.oauth2server.verify.TokenService
import uk.gov.service.notify.NotificationClientApi

@Service
@Transactional(transactionManager = "authTransactionManager", readOnly = true)
open class MfaService(@Value("\${application.authentication.mfa.whitelist}") whitelist: Set<String>,
                      @Value("\${application.authentication.mfa.roles}") private val mfaRoles: Set<String>,
                      @Value("\${application.notify.mfa.template}") private val mfaTemplateId: String,
                      private val tokenService: TokenService,
                      private val userService: UserService,
                      private val notificationClient: NotificationClientApi,
                      private val userRetriesService: UserRetriesService) {

  private val ipMatchers: List<IpAddressMatcher>

  init {
    ipMatchers = whitelist.map { ip -> IpAddressMatcher(ip) }
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  open fun needsMfa(authorities: Collection<GrantedAuthority>): Boolean {
    // if they're whitelisted then no mfa
    val ip = IpAddressHelper.retrieveIpFromRequest()
    return if (ipMatchers.any { it.matches(ip) }) {
      false
      // otherwise check that they have a role that requires mfa
    } else authorities.stream().map { it.authority }.anyMatch { r -> mfaRoles.contains(r) }
  }

  @Transactional(transactionManager = "authTransactionManager")
  open fun createTokenAndSendEmail(username: String): Pair<String, String> {
    log.info("Creating token and sending email for {}", username)
    val user = userService.getOrCreateUser(username)

    val token = tokenService.createToken(TokenType.MFA, username)
    val code = tokenService.createToken(TokenType.MFA_CODE, username)

    emailCode(user, code)

    return Pair(token, code)
  }

  @Transactional(transactionManager = "authTransactionManager", noRollbackFor = [LoginFlowException::class, MfaFlowException::class])
  open fun validateAndRemoveMfaCode(token: String, code: String?) {
    if (code.isNullOrBlank()) throw MfaFlowException("missingcode")

    val userToken = tokenService.getToken(TokenType.MFA, token).orElseThrow()
    val userPersonDetails = userService.findMasterUserPersonDetails(userToken.user.username).orElseThrow()

    if (!userPersonDetails.isAccountNonLocked) throw LoginFlowException("locked")

    val errors = tokenService.checkToken(TokenType.MFA_CODE, code)
    errors.ifPresent {
      if (it == "invalid") {
        val locked = userRetriesService.incrementRetriesAndLockAccountIfNecessary(userPersonDetails)
        if (locked) throw LoginFlowException("locked")
      }
      throw MfaFlowException(it)
    }

    tokenService.removeToken(TokenType.MFA, token)
    tokenService.removeToken(TokenType.MFA_CODE, code)

    userRetriesService.resetRetries(userPersonDetails.username)
  }

  open fun resendMfaCode(token: String): String? {
    val userToken = tokenService.getToken(TokenType.MFA, token).orElseThrow()

    val code = userToken.user.tokens.filter { it.tokenType == TokenType.MFA_CODE }.map { it.token }.firstOrNull()

    code?.run { emailCode(userToken.user, code) }

    return code
  }

  private fun emailCode(user: User, code: String) {
    val firstName = userService.findMasterUserPersonDetails(user.username).map { it.firstName }.orElseThrow()

    notificationClient.sendEmail(mfaTemplateId, user.email, mapOf("firstName" to firstName, "code" to code), null)
  }
}

class MfaFlowException(val error: String) : RuntimeException(error)
class LoginFlowException(val error: String) : RuntimeException(error)