package uk.gov.justice.digital.hmpps.oauth2server.integration

import org.assertj.core.api.Assertions.assertThat
import org.fluentlenium.core.annotation.PageUrl
import org.fluentlenium.core.domain.FluentWebElement
import org.junit.jupiter.api.Test
import org.openqa.selenium.support.FindBy

class LoginSpecification : AbstractAuthSpecification() {
  @Test
  fun `Log in with valid auth credentials`() {
    val homePage = goTo(loginPage).loginAs("AUTH_USER")
    homePage.assertNameDisplayedCorrectly("Auth Only")
  }

  @Test
  fun `Log in with valid delius credentials`() {
    val homePage = goTo(loginPage).loginAs("DELIUS_USER", "password")
    homePage.assertNameDisplayedCorrectly("Delius Smith")
  }
}

@PageUrl("/login")
class LoginPage : AuthPage<LoginPage>("HMPPS Digital Services - Sign in", "Sign in") {
  @FindBy(css = "input[type='submit']")
  private lateinit var signInButton: FluentWebElement

  @FindBy(css = "input[name='username']")
  private lateinit var username: FluentWebElement

  @FindBy(css = "input[name='password']")
  private lateinit var password: FluentWebElement

  fun loginAsWithUnverifiedEmail(username: String, password: String = "password123456"): VerifyEmailPage {
    this.username.fill().withText(username)
    this.password.fill().withText(password)
    signInButton.submit()

    val verifyEmailPage = newInstance(VerifyEmailPage::class.java)
    verifyEmailPage.isAt()
    return verifyEmailPage
  }

  fun loginAs(username: String, password: String = "password123456"): HomePage {
    this.username.fill().withText(username)
    this.password.fill().withText(password)
    signInButton.submit()

    val homePage = newInstance(HomePage::class.java)
    homePage.isAt()
    return homePage
  }

  fun viewContact() {
    val contactLink = el("a[data-qa='contact']")
    assertThat(contactLink.text()).isEqualTo("Contact")
    contactLink.click()
  }

  fun viewTerms() {
    val termsLink = el("a[data-qa='terms']")
    assertThat(termsLink.text()).isEqualTo("Terms and conditions")
    termsLink.click()
  }
}