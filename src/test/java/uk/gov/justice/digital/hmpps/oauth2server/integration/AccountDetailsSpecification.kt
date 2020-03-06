package uk.gov.justice.digital.hmpps.oauth2server.integration

import org.assertj.core.api.Assertions.assertThat
import org.fluentlenium.core.annotation.Page
import org.fluentlenium.core.annotation.PageUrl
import org.junit.jupiter.api.Test


class AccountDetailsSpecification : AbstractAuthSpecification() {
  @Page
  private lateinit var accountDetailsPage: AccountDetailsPage

  @Test
  fun `auth account details`() {
    goTo(loginPage).loginAs("AUTH_RO_USER")
        .navigateToAccountDetails()

    accountDetailsPage.isAt()
    accountDetailsPage.checkAuthDetails()
  }

  @Test
  fun `nomis account details`() {
    goTo(loginPage).loginAs("ITAG_USER", "password")

    goTo(accountDetailsPage).checkNomisDetails()
  }

  @Test
  fun `unverified account details`() {
    goTo(loginPage).loginAsWithUnverifiedEmail("AUTH_UNVERIFIED")

    goTo(accountDetailsPage).checkUnverified()
  }

  @Test
  fun `cancel account details`() {
    val homePage = goTo(loginPage).loginAs("AUTH_RO_USER")
    homePage.navigateToAccountDetails()
    accountDetailsPage.isAt()

    accountDetailsPage.cancel()

    homePage.isAt()
  }

  @Test
  fun `navigation - change name`() {
    goTo(loginPage).loginAs("AUTH_RO_USER")
        .navigateToAccountDetails()

    accountDetailsPage.navigateToChangeName()
        .cancel()

    accountDetailsPage.isAt()
  }
}

@PageUrl("/account-details")
class AccountDetailsPage : AuthPage<AccountDetailsPage>("HMPPS Digital Services - Account Details", "Account details") {
  @Suppress("UsePropertyAccessSyntax")
  fun checkAuthDetails(): AccountDetailsPage {
    assertThat(el("[data-qa='username']").text()).isEqualTo("AUTH_RO_USER")
    assertThat(el("[data-qa='name']").text()).isEqualTo("Ryan-Auth Orton")
    assertThat(el("[data-qa='lastLoggedIn']").text()).isNotBlank()
    assertThat(el("[data-qa='changeName']").text()).isEqualToNormalizingWhitespace("Change name")
    assertThat(el("[data-qa='passwordExpiry']").text()).isEqualTo("************")
    assertThat(el("[data-qa='email']").text()).isEqualTo("auth_ro_user@digital.justice.gov.uk")
    assertThat(el("[data-qa='changeEmail']").text()).isEqualToNormalizingWhitespace("Change email")
    assertThat(el("[data-qa='verified']").text()).isEqualTo("yes")
    assertThat(find("[data-qa='verifyEmail']")).isEmpty()
    assertThat(el("[data-qa='mobile']").text()).isBlank()
    assertThat(el("[data-qa='changeMobile']").text()).isEqualToNormalizingWhitespace("Add mobile")
    assertThat(el("[data-qa='mobileVerified']").text()).isEqualTo("no")
    assertThat(find("[data-qa='verifyMobile']")).isEmpty()
    return this
  }

  fun checkNomisDetails(): AccountDetailsPage {
    assertThat(el("[data-qa='username']").text()).isEqualTo("ITAG_USER")
    assertThat(el("[data-qa='name']").text()).isEqualTo("Itag User")
    // change name only available for auth users
    assertThat(find("[data-qa='changeName']")).isEmpty()
    assertThat(el("[data-qa='email']").text()).isEqualTo("itag_user@digital.justice.gov.uk")
    assertThat(el("[data-qa='changeEmail']").text()).isEqualToNormalizingWhitespace("Change email")
    return this
  }

  fun checkUnverified(): AccountDetailsPage {
    assertThat(el("[data-qa='username']").text()).isEqualTo("AUTH_UNVERIFIED")
    assertThat(el("[data-qa='email']").text()).isEqualTo("auth_unverified@digital.justice.gov.uk")
    assertThat(el("[data-qa='changeEmail']").text()).isEqualToNormalizingWhitespace("Change email")
    assertThat(el("[data-qa='verified']").text()).isEqualTo("no")
    assertThat(el("[data-qa='verifyEmail']").text()).isEqualToNormalizingWhitespace("Resend email code")
    assertThat(el("[data-qa='mobile']").text()).isEqualTo("07700900321")
    assertThat(el("[data-qa='changeMobile']").text()).isEqualToNormalizingWhitespace("Change mobile")
    assertThat(el("[data-qa='mobileVerified']").text()).isEqualTo("no")
    assertThat(el("[data-qa='verifyMobile']").text()).isEqualToNormalizingWhitespace("Resend mobile code")
    return this
  }

  fun navigateToChangeName(): UserDetailsPage {
    el("[data-qa='changeName']").click()
    val userDetailsPage = newInstance(UserDetailsPage::class.java)
    userDetailsPage.isAt()
    return userDetailsPage
  }

  fun cancel() {
    el("[data-qa='back-link']").click()
  }
}