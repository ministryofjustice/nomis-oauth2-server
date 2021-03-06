package uk.gov.justice.digital.hmpps.oauth2server.security

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.util.ReflectionTestUtils
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountDetail
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountStatus
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountStatus.EXPIRED
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountStatus.EXPIRED_GRACE
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountStatus.EXPIRED_LOCKED
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountStatus.LOCKED
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountStatus.LOCKED_TIMED
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.AccountStatus.OPEN
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.NomisUserPersonDetails
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.Role
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.Staff
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.UserCaseloadRole
import uk.gov.justice.digital.hmpps.oauth2server.nomis.model.UserCaseloadRoleIdentity
import java.util.Optional
import javax.persistence.EntityManager

class NomisUserDetailsServiceTest {
  private val userService: NomisUserService = mock()
  private val nomisEntityManager: EntityManager = mock()
  private val service = NomisUserDetailsService(userService)

  @BeforeEach
  fun setup() {
    ReflectionTestUtils.setField(service, "nomisEntityManager", nomisEntityManager)
  }

  @Test
  fun testHappyUserPath() {
    val user = buildStandardUser("ITAG_USER")
    whenever(userService.getNomisUserByUsername(user.username)).thenReturn(Optional.of(user))
    val itagUser = service.loadUserByUsername(user.username)
    assertThat(itagUser).isNotNull()
    assertThat(itagUser.isAccountNonExpired).isTrue()
    assertThat(itagUser.isAccountNonLocked).isTrue()
    assertThat(itagUser.isCredentialsNonExpired).isTrue()
    assertThat(itagUser.isEnabled).isTrue()
    assertThat((itagUser as UserPersonDetails).name).isEqualTo("Itag User")
  }

  @Test
  fun testEntityDetached() {
    val user = buildStandardUser("ITAG_USER")
    whenever(userService.getNomisUserByUsername(user.username)).thenReturn(Optional.of(user))
    val itagUser = service.loadUserByUsername(user.username)
    Mockito.verify(nomisEntityManager).detach(user)
    assertThat((itagUser as UserPersonDetails).name).isEqualTo("Itag User")
  }

  @Test
  fun testLockedUser() {
    val user = buildLockedUser()
    whenever(userService.getNomisUserByUsername(user.username)).thenReturn(Optional.of(user))
    val itagUser = service.loadUserByUsername(user.username)
    assertThat(itagUser).isNotNull()
    assertThat(itagUser.isAccountNonExpired).isTrue()
    assertThat(itagUser.isAccountNonLocked).isFalse()
    assertThat(itagUser.isCredentialsNonExpired).isTrue()
    assertThat(itagUser.isEnabled).isFalse()
  }

  @Test
  fun testExpiredUser() {
    val user = buildExpiredUser()
    whenever(userService.getNomisUserByUsername(user.username)).thenReturn(Optional.of(user))
    val itagUser = service.loadUserByUsername(user.username)
    assertThat(itagUser).isNotNull()
    assertThat(itagUser.isAccountNonExpired).isTrue()
    assertThat(itagUser.isAccountNonLocked).isTrue()
    assertThat(itagUser.isCredentialsNonExpired).isFalse()
    assertThat(itagUser.isEnabled).isTrue()
  }

  @Test
  fun testUserNotFound() {
    whenever(userService.getNomisUserByUsername(anyString())).thenReturn(Optional.empty())
    assertThatThrownBy { service.loadUserByUsername("user") }.isInstanceOf(UsernameNotFoundException::class.java)
  }

  @Test
  fun testExpiredGraceUser() {
    val user = buildExpiredGraceUser()
    whenever(userService.getNomisUserByUsername(user.username)).thenReturn(Optional.of(user))
    val itagUser = service.loadUserByUsername(user.username)
    assertThat(itagUser).isNotNull()
    assertThat(itagUser.isAccountNonExpired).isTrue()
    assertThat(itagUser.isAccountNonLocked).isTrue()
    assertThat(itagUser.isCredentialsNonExpired).isTrue()
    assertThat(itagUser.isEnabled).isTrue()
  }

  @Test
  fun testExpiredLockedUser() {
    val user = buildExpiredLockedUser()
    whenever(userService.getNomisUserByUsername(user.username)).thenReturn(Optional.of(user))
    val itagUser = service.loadUserByUsername(user.username)
    assertThat(itagUser).isNotNull()
    assertThat(itagUser.isAccountNonLocked).isFalse()
    assertThat(itagUser.isCredentialsNonExpired).isFalse()
    assertThat(itagUser.isEnabled).isFalse()
  }

  @Test
  fun testLockedTimedUser() {
    val user = buildLockedTimedUser()
    whenever(userService.getNomisUserByUsername(user.username)).thenReturn(Optional.of(user))
    val itagUser = service.loadUserByUsername(user.username)
    assertThat(itagUser).isNotNull()
    assertThat(itagUser.isEnabled).isFalse()
    assertThat(itagUser.isAccountNonExpired).isTrue()
    assertThat(itagUser.isAccountNonLocked).isFalse()
    assertThat(itagUser.isCredentialsNonExpired).isTrue()
  }

  private fun buildStandardUser(username: String, accountDetail: AccountDetail = buildAccountDetail(username, OPEN)): NomisUserPersonDetails {
    val staff = buildStaff()
    val roles = listOf(
      UserCaseloadRole(
        id = UserCaseloadRoleIdentity(caseload = "NWEB", roleId = ROLE_ID, username = username),
        role = Role(code = "ROLE1", id = ROLE_ID)
      )
    )
    return NomisUserPersonDetails(username = username, password = "pass", type = "GENERAL", staff = staff, roles = roles, accountDetail = accountDetail, activeCaseLoadId = null)
  }

  private fun buildExpiredUser(): NomisUserPersonDetails =
    buildStandardUser("EXPIRED_USER", buildAccountDetail("EXPIRED_USER", EXPIRED))

  private fun buildLockedUser(): NomisUserPersonDetails =
    buildStandardUser("LOCKED_USER", buildAccountDetail("LOCKED_USER", LOCKED))

  private fun buildExpiredLockedUser(): NomisUserPersonDetails =
    buildStandardUser("EXPIRED_USER", buildAccountDetail("EXPIRED_USER", EXPIRED_LOCKED))

  private fun buildLockedTimedUser(): NomisUserPersonDetails =
    buildStandardUser("LOCKED_USER", buildAccountDetail("LOCKED_USER", LOCKED_TIMED))

  private fun buildExpiredGraceUser(): NomisUserPersonDetails =
    buildStandardUser("EXPIRED_USER", buildAccountDetail("EXPIRED_USER", EXPIRED_GRACE))

  private fun buildAccountDetail(username: String, status: AccountStatus): AccountDetail {
    return AccountDetail(
      username = username,
      accountStatus = status.desc,
      profile = "TAG_GENERAL"
    )
  }

  private fun buildStaff(): Staff = Staff(firstName = "ITAG", status = "ACTIVE", lastName = "USER", staffId = 1)

  companion object {
    private const val ROLE_ID = 1L
  }
}
