package uk.gov.justice.digital.hmpps.oauth2server.verify

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.verify
import org.springframework.security.authentication.LockedException
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.Person
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.User
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.UserToken
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.UserRepository
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.UserTokenRepository
import uk.gov.justice.digital.hmpps.oauth2server.delius.model.DeliusUserPersonDetails
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountDetail
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountStatus
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.NomisUserPersonDetails
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.Role
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.Staff
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.UserCaseloadRole
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.UserCaseloadRoleIdentity
import uk.gov.justice.digital.hmpps.oauth2server.security.AuthSource
import uk.gov.justice.digital.hmpps.oauth2server.security.UserPersonDetails
import uk.gov.justice.digital.hmpps.oauth2server.security.UserService
import uk.gov.justice.digital.hmpps.oauth2server.service.DelegatingUserService
import uk.gov.service.notify.NotificationClientApi
import uk.gov.service.notify.NotificationClientException
import java.util.*
import java.util.Map.entry

class ResetPasswordServiceTest {
  private val userRepository: UserRepository = mock()
  private val userTokenRepository: UserTokenRepository = mock()
  private val userService: UserService = mock()
  private val delegatingUserService: DelegatingUserService = mock()
  private val notificationClient: NotificationClientApi = mock()
  private val verifyEmailService: VerifyEmailService = mock()
  private val resetPasswordService = ResetPasswordServiceImpl(userRepository, userTokenRepository,
      userService, delegatingUserService, notificationClient, verifyEmailService,
      "resetTemplate", "resetUnavailableTemplate", "resetUnavailableEmailNotFoundTemplate", "reset-confirm")

  @Test
  fun requestResetPassword_noUserEmail() {
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.empty())
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(Optional.empty())
    val optional = resetPasswordService.requestResetPassword("user", "url")
    assertThat(optional).isEmpty
  }

  @Test
  fun requestResetPassword_noEmail() {
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(User.of("user")))
    val optional = resetPasswordService.requestResetPassword("user", "url")
    assertThat(optional).isEmpty
  }

  @Test
  fun requestResetPassword_noNomisUser() {
    val user = User.builder().username("USER").email("email").verified(true).build()
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(Optional.empty())
    val optional = resetPasswordService.requestResetPassword("user", "url")
    verify(notificationClient).sendEmail(eq("resetUnavailableTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "USER"), entry("fullName", "USER"))
    }, isNull())
    assertThat(optional).isEmpty
  }

  @Test
  fun requestResetPassword_inactive() {
    val user = User.builder().username("someuser").email("email").source(AuthSource.nomis).verified(true).build()
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    val staffUserAccount = staffUserAccountForBobOptional
    (staffUserAccount.orElseThrow() as NomisUserPersonDetails).staff.status = "inactive"
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(staffUserAccount)
    val optional = resetPasswordService.requestResetPassword("user", "url")
    verify(notificationClient).sendEmail(eq("resetUnavailableTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "Bob"), entry("fullName", "Bob Smith"))
    }, isNull())
    assertThat(optional).isEmpty
  }

  @Test
  fun requestResetPassword_authLocked() {
    val user = User.builder().username("someuser").email("email").verified(true).locked(true).build()
    user.person = Person("Bob", "Smith")
    user.source = AuthSource.auth
    user.isEnabled = true
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    val optionalLink = resetPasswordService.requestResetPassword("user", "url")
    verify(notificationClient).sendEmail(eq("resetTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "Bob"), entry("fullName", "Bob Smith"), entry("resetLink", optionalLink.get()))
    }, isNull())
    assertThat(optionalLink).isPresent
  }

  @Test
  fun requestResetPassword_notAuthLocked() {
    val user = User.builder().username("someuser").email("email").verified(true).build()
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    val accountOptional = staffUserAccountForBobOptional
    (accountOptional.orElseThrow() as NomisUserPersonDetails).accountDetail.accountStatus = "LOCKED"
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(accountOptional)
    val optional = resetPasswordService.requestResetPassword("user", "url")
    verify(notificationClient).sendEmail(eq("resetUnavailableTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "Bob"), entry("fullName", "Bob Smith"))
    }, isNull())
    assertThat(optional).isEmpty
  }

  @Test
  fun requestResetPassword_userLocked() {
    val user = User.builder().username("someuser").email("email").source(AuthSource.nomis).verified(true).build()
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    val accountOptional = staffUserAccountForBobOptional
    (accountOptional.orElseThrow() as NomisUserPersonDetails).accountDetail.accountStatus = "EXPIRED & LOCKED(TIMED)"
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(accountOptional)
    val optionalLink = resetPasswordService.requestResetPassword("user", "url")
    verify(notificationClient).sendEmail(eq("resetTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "Bob"), entry("fullName", "Bob Smith"), entry("resetLink", optionalLink.get()))
    }, isNull())
    assertThat(optionalLink).isPresent
  }

  @Test
  fun requestResetPassword_existingToken() {
    val user = User.builder().username("someuser").email("email").verified(true).build()
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(staffUserAccountForBobOptional)
    val existingUserToken = user.createToken(UserToken.TokenType.RESET)
    resetPasswordService.requestResetPassword("user", "url")
    assertThat(user.tokens).hasSize(1).extracting<String> { obj: UserToken -> obj.token }.doesNotContain(existingUserToken.token)
  }

  @Test
  fun requestResetPassword_verifyToken() {
    val user = User.builder().username("someuser").person(Person("Bob", "Smith")).email("email").locked(true).build()
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(staffUserAccountForBobOptional)
    val optionalLink = resetPasswordService.requestResetPassword("user", "url")
    verify(notificationClient).sendEmail(eq("resetTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "Bob"), entry("fullName", "Bob Smith"), entry("resetLink", optionalLink.get()))
    }, isNull())
    assertThat(optionalLink).isPresent
  }

  @Test
  fun requestResetPassword_uppercaseUsername() {
    val user = User.builder().username("SOMEUSER").email("email").locked(true).build()
    whenever(userRepository.findByUsername(any())).thenReturn(Optional.of(user))
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(staffUserAccountForBobOptional)
    resetPasswordService.requestResetPassword("someuser", "url")
    verify(userRepository).findByUsername("SOMEUSER")
    verify(userService).findMasterUserPersonDetails("SOMEUSER")
  }

  @Test
  fun requestResetPassword_verifyNotification() {
    val user = User.builder().username("someuser").email("email").locked(true).build()
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(staffUserAccountForBobOptional)
    val linkOptional = resetPasswordService.requestResetPassword("user", "url")
    val value = user.tokens.stream().findFirst().orElseThrow()
    assertThat(linkOptional).get().isEqualTo(String.format("url-confirm?token=%s", value.token))
    assertThat(value.tokenType).isEqualTo(UserToken.TokenType.RESET)
    assertThat(value.user.email).isEqualTo("email")
  }

  @Test
  fun requestResetPassword_sendFailure() {
    val user = User.builder().username("someuser").email("email").locked(true).build()
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(staffUserAccountForBobOptional)
    whenever(notificationClient.sendEmail(anyString(), anyString(), anyMap<String, Any?>(), isNull())).thenThrow(NotificationClientException("message"))
    assertThatThrownBy { resetPasswordService.requestResetPassword("user", "url") }.hasMessageContaining("NotificationClientException: message")
  }

  @Test
  fun requestResetPassword_emailAddressNotFound() {
    whenever(userRepository.findByEmail(any())).thenReturn(emptyList())
    val optional = resetPasswordService.requestResetPassword("someuser@somewhere", "url")
    verify(notificationClient).sendEmail(eq("resetUnavailableEmailNotFoundTemplate"), eq("someuser@somewhere"), check {
      assertThat(it).isEmpty()
    }, isNull())
    assertThat(optional).isEmpty
  }

  @Test
  fun requestResetPassword_emailAddressNotFound_formatEmailInput() {
    whenever(userRepository.findByEmail(any())).thenReturn(emptyList())
    val optional = resetPasswordService.requestResetPassword("some.u’ser@SOMEwhere", "url")
    verify(notificationClient).sendEmail(eq("resetUnavailableEmailNotFoundTemplate"), eq("some.u'ser@somewhere"), check {
      assertThat(it).isEmpty()
    }, isNull())
    assertThat(optional).isEmpty
  }

  @Test
  fun requestResetPassword_multipleEmailAddresses() {
    val user = User.builder().username("someuser").email("email")
        .person(Person("Bob", "Smith"))
        .source(AuthSource.auth)
        .enabled(true)
        .build()
    whenever(userRepository.findByEmail(any())).thenReturn(listOf(user, user))
    val optional = resetPasswordService.requestResetPassword("someuser@somewhere", "http://url")
    verify(notificationClient).sendEmail(eq("resetTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "Bob"), entry("fullName", "Bob Smith"), entry("resetLink", optional.get()))
    }, isNull())
    assertThat(optional).isPresent
  }

  @Test
  fun requestResetPassword_multipleEmailAddresses_verifyToken() {
    val user = User.builder().username("someuser").email("email")
        .person(Person("Bob", "Smith"))
        .source(AuthSource.auth)
        .enabled(true)
        .build()
    whenever(userRepository.findByEmail(any())).thenReturn(listOf(user, user))
    val linkOptional = resetPasswordService.requestResetPassword("someuser@somewhere", "http://url")
    val userToken = user.tokens.stream().findFirst().orElseThrow()
    assertThat(linkOptional).get().isEqualTo(String.format("http://url-select?token=%s", userToken.token))
  }

  @Test
  fun requestResetPassword_multipleEmailAddresses_noneCanBeReset() {
    val user = User.builder().username("someuser").email("email").build()
    user.person = Person("Bob", "Smith")
    user.source = AuthSource.auth
    whenever(userRepository.findByEmail(any())).thenReturn(listOf(user, user))
    val optional = resetPasswordService.requestResetPassword("someuser@somewhere", "http://url")
    verify(notificationClient).sendEmail(eq("resetUnavailableTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "Bob"), entry("fullName", "Bob Smith"))
    }, isNull())
    assertThat(optional).isEmpty
  }

  @Test
  fun requestResetPassword_byEmail() {
    val user = User.builder().username("someuser").email("email").locked(true).build()
    whenever(userRepository.findByEmail(anyString())).thenReturn(listOf(user))
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(staffUserAccountForBobOptional)
    val optionalLink = resetPasswordService.requestResetPassword("user@where", "url")
    verify(notificationClient).sendEmail(eq("resetTemplate"), eq("email"), check {
      assertThat(it).containsOnly(entry("firstName", "Bob"), entry("fullName", "Bob Smith"), entry("resetLink", optionalLink.get()))
    }, isNull())
    assertThat(optionalLink).isPresent
  }

  private val staffUserAccountForBob: UserPersonDetails
    get() {
      val staffUserAccount = NomisUserPersonDetails()
      val staff = Staff()
      staff.firstName = "bOb"
      staff.lastName = "Smith"
      staff.status = "ACTIVE"
      staffUserAccount.staff = staff
      val detail = AccountDetail("user", "OPEN", "profile", null)
      staffUserAccount.accountDetail = detail
      return staffUserAccount
    }

  private val staffUserAccountForBobOptional: Optional<UserPersonDetails> = Optional.of(staffUserAccountForBob)

  @Test
  fun resetPassword() {
    val staffUserAccountForBob = staffUserAccountForBob
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(Optional.of(staffUserAccountForBob))
    val user = User.of("user")
    user.person = Person("First", "Last")
    val userToken = user.createToken(UserToken.TokenType.RESET)
    whenever(userTokenRepository.findById(anyString())).thenReturn(Optional.of(userToken))
    resetPasswordService.setPassword("bob", "pass")
    assertThat(user.tokens).isEmpty()
    verify(userRepository).save(user)
    verify(delegatingUserService).changePasswordWithUnlock(staffUserAccountForBob, "pass")
    verify(notificationClient).sendEmail("reset-confirm", null, mapOf("firstName" to "First", "fullName" to "First Last", "username" to "user"), null)
  }

  @Test
  fun resetPassword_authUser() {
    val user = User.builder().username("user").person(Person("First", "Last")).enabled(true).source(AuthSource.auth).build()
    val userToken = user.createToken(UserToken.TokenType.RESET)
    whenever(userTokenRepository.findById(anyString())).thenReturn(Optional.of(userToken))
    resetPasswordService.setPassword("bob", "pass")
    assertThat(user.tokens).isEmpty()
    verify(userRepository).save(user)
    verify(delegatingUserService).changePasswordWithUnlock(any(), anyString())
    verify(notificationClient).sendEmail("reset-confirm", null, mapOf("firstName" to "First", "fullName" to "First Last", "username" to "user"), null)
  }

  @Test
  fun resetPassword_deliusUser() {
    val user = User.builder().username("user").person(Person("First", "Last")).enabled(true).source(AuthSource.delius).build()
    val userToken = user.createToken(UserToken.TokenType.RESET)
    val deliusUserPersonDetails = DeliusUserPersonDetails("user", "12345", "Delius", "Smith", "a@b.com", true, false, setOf())
    whenever(userService.findMasterUserPersonDetails("user")).thenReturn(Optional.of(deliusUserPersonDetails))
    whenever(userTokenRepository.findById(anyString())).thenReturn(Optional.of(userToken))
    resetPasswordService.setPassword("bob", "pass")
    assertThat(user.tokens).isEmpty()
    verify(userRepository).save(user)
    verify(delegatingUserService).changePasswordWithUnlock(any(), anyString())
    verify(notificationClient).sendEmail("reset-confirm", null, mapOf("firstName" to "First", "fullName" to "First Last", "username" to "user"), null)
  }

  @Test
  fun resetPasswordLockedAccount() {
    val staffUserAccount = staffUserAccountForBobOptional
    (staffUserAccount.orElseThrow() as NomisUserPersonDetails).staff.status = "inactive"
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(staffUserAccount)
    val user = User.builder().username("user").source(AuthSource.nomis).build()
    val userToken = user.createToken(UserToken.TokenType.RESET)
    whenever(userTokenRepository.findById(anyString())).thenReturn(Optional.of(userToken))
    assertThatThrownBy { resetPasswordService.setPassword("bob", "pass") }.isInstanceOf(LockedException::class.java)
  }

  @Test
  fun resetPasswordLockedAccount_authUser() {
    val user = User.of("user")
    user.source = AuthSource.auth
    val userToken = user.createToken(UserToken.TokenType.RESET)
    whenever(userTokenRepository.findById(anyString())).thenReturn(Optional.of(userToken))
    assertThatThrownBy { resetPasswordService.setPassword("bob", "pass") }.isInstanceOf(LockedException::class.java)
  }

  @Test
  fun moveTokenToAccount_missingUsername() {
    assertThatThrownBy { resetPasswordService.moveTokenToAccount("token", "  ") }.hasMessageContaining("failed with reason: missing")
  }

  @Test
  fun moveTokenToAccount_usernameNotFound() {
    assertThatThrownBy { resetPasswordService.moveTokenToAccount("token", "noone") }.hasMessageContaining("failed with reason: notfound")
  }

  @Test
  fun moveTokenToAccount_differentEmail() {
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(User.builder().username("user").email("email").verified(true).build()))
    val builtUser = User.builder().username("other").email("emailother").verified(true).build()
    whenever(userTokenRepository.findById("token")).thenReturn(Optional.of(builtUser.createToken(UserToken.TokenType.RESET)))
    assertThatThrownBy { resetPasswordService.moveTokenToAccount("token", "noone") }.hasMessageContaining("failed with reason: email")
  }

  @Test
  fun moveTokenToAccount_disabled() {
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(User.builder().username("user").email("email").verified(true).build()))
    val builtUser = User.builder().username("other").email("email").verified(true).build()
    whenever(userTokenRepository.findById("token")).thenReturn(Optional.of(builtUser.createToken(UserToken.TokenType.RESET)))
    assertThatThrownBy { resetPasswordService.moveTokenToAccount("token", "noone") }.extracting("reason").isEqualTo("locked")
  }

  @Test
  fun moveTokenToAccount_sameUserAccount() {
    val user = User.builder().username("USER").email("email").verified(true).build()
    user.isEnabled = true
    user.source = AuthSource.auth
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    whenever(userTokenRepository.findById("token")).thenReturn(Optional.of(user.createToken(UserToken.TokenType.RESET)))
    val newToken = resetPasswordService.moveTokenToAccount("token", "USER")
    assertThat(newToken).isEqualTo("token")
  }

  @Test
  fun moveTokenToAccount_differentAccount() {
    val user = User.builder().username("USER").email("email").verified(true).build()
    user.isEnabled = true
    user.source = AuthSource.auth
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user))
    val builtUser = User.builder().username("other").email("email").verified(true).build()
    val userToken = builtUser.createToken(UserToken.TokenType.RESET)
    whenever(userTokenRepository.findById("token")).thenReturn(Optional.of(userToken))
    val newToken = resetPasswordService.moveTokenToAccount("token", "USER")
    assertThat(newToken).hasSize(36)
    verify(userTokenRepository).delete(userToken)
    assertThat(user.tokens).extracting<String> { obj: UserToken -> obj.token }.containsExactly(newToken)
  }

  @Test
  fun `Nomis User who has not logged into auth can reset password`() {
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.empty())
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(Optional.of(buildStandardUser("user")))
    val emailAddresses = mutableListOf("Bob.smith@hmps.gsi.gov.uk", "Bob.smith@justice.gov.uk")
    whenever(verifyEmailService.getExistingEmailAddresses(anyString())).thenReturn(emailAddresses)
    val optionalLink = resetPasswordService.requestResetPassword("user", "url")
    assertThat(optionalLink).isPresent
  }

  @Test
  fun `Nomis User who has not logged reset password request no email`() {
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.empty())
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(Optional.of(buildStandardUser("user")))
    whenever(verifyEmailService.getExistingEmailAddresses(anyString())).thenReturn(mutableListOf())
    val optional = resetPasswordService.requestResetPassword("user", "url")
    assertThat(optional).isEmpty
  }

  @Test
  fun `Nomis User who has not logged reset password request multiple justice emails`() {
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.empty())
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(Optional.of(buildStandardUser("user")))
    val emailAddresses = mutableListOf("Bob.smith@hmps.gsi.gov.uk", "Bob.smith@justice.gov.uk", "Bob.smith2@justice.gov.uk")
    whenever(verifyEmailService.getExistingEmailAddresses(anyString())).thenReturn(emailAddresses)
    val optional = resetPasswordService.requestResetPassword("user", "url")
    assertThat(optional).isEmpty
  }

  @Test
  fun `Delius User who has not logged reset password not yet`() {
    whenever(userRepository.findByUsername(anyString())).thenReturn(Optional.empty())
    whenever(userService.findMasterUserPersonDetails(anyString())).thenReturn(Optional.of(createDeliusUser()))
    val optional = resetPasswordService.requestResetPassword("user", "url")
    assertThat(optional).isEmpty
  }

  private fun createDeliusUser() =
      DeliusUserPersonDetails(username = "user", userId = "12345", firstName = "F", surname = "L", email = "a@b.com")

  private fun buildStandardUser(username: String): NomisUserPersonDetails {
    val staff = buildStaff()
    return NomisUserPersonDetails.builder()
        .username(username)
        .password("pass")
        .type("GENERAL")
        .staff(staff)
        .roles(listOf(UserCaseloadRole.builder()
            .id(UserCaseloadRoleIdentity.builder().caseload("NWEB").roleId(ROLE_ID).username(username).build())
            .role(Role.builder().code("ROLE1").id(ROLE_ID).build())
            .build()))
        .accountDetail(buildAccountDetail(username, AccountStatus.OPEN))
        .build()
  }

  private fun buildStaff(): Staff {
    return Staff.builder()
        .staffId(1L)
        .firstName("Bob")
        .lastName("Smith")
        .status("ACTIVE")
        .build()
  }

  private fun buildAccountDetail(username: String, status: AccountStatus): AccountDetail {
    return AccountDetail.builder()
        .username(username)
        .accountStatus(status.desc)
        .profile("TAG_GENERAL")
        .build()
  }

  companion object {
    const val ROLE_ID = 1L
  }

}
