package uk.gov.justice.digital.hmpps.oauth2server.verify

import com.microsoft.applicationinsights.TelemetryClient
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.User
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.UserRepository
import uk.gov.service.notify.NotificationClientApi

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("dev")
@Transactional(transactionManager = "authTransactionManager")
open class VerifyEmailServiceIntTest {
  @Autowired
  private lateinit var jdbcTemplate: JdbcTemplate
  private lateinit var verifyEmailService: VerifyEmailService
  private val telemetryClient: TelemetryClient = mock()
  private val notificationClient: NotificationClientApi = mock()
  @Autowired
  private lateinit var referenceCodesService: ReferenceCodesService
  @Autowired
  private lateinit var userRepository: UserRepository

  @BeforeEach
  fun setUp() {
    verifyEmailService = VerifyEmailService(userRepository, null, jdbcTemplate, telemetryClient, notificationClient, referenceCodesService, "templateId")
  }

  @Test
  fun existingEmailAddresses() {
    val emails = verifyEmailService.getExistingEmailAddresses("RO_USER")
    assertThat(emails).containsExactlyInAnyOrder("phillips@bobjustice.gov.uk", "phillips@fredjustice.gov.uk")
  }

  @Test
  fun existingEmailAddresses_NotFound() {
    val emails = verifyEmailService.getExistingEmailAddresses("CA_USER")
    assertThat(emails).isEmpty()
  }

  @Test
  fun emailAddressSetToNotVerified() {
    val userBefore = userRepository.findByUsername("AUTH_CHANGE_EMAIL")
    assertThat(userBefore.get().isVerified).isTrue()
    verifyEmailService.requestVerification("AUTH_CHANGE_EMAIL", "phillips@fredjustice.gov.uk", "AUTH", "full name", "url", User.EmailType.PRIMARY)
    val userAfter = userRepository.findByUsername("AUTH_CHANGE_EMAIL")
    assertThat(userAfter.get().isVerified).isFalse()
  }

  @Test
  fun secondaryEmailAddressSetToNotVerified() {
    val userBefore = userRepository.findByUsername("AUTH_SECOND_EMAIL_CHANGE")
    assertThat(userBefore.get().isSecondaryEmailVerified).isTrue()
    verifyEmailService.requestVerification("AUTH_CHANGE_EMAIL", "phillips@fredjustice.gov.uk", "AUTH", "full name", "url", User.EmailType.SECONDARY)
    val userAfter = userRepository.findByUsername("AUTH_CHANGE_EMAIL")
    assertThat(userAfter.get().isSecondaryEmailVerified).isFalse()
  }
}
