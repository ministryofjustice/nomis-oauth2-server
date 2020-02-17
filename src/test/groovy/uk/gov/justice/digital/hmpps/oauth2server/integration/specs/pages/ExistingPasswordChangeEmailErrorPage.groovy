package uk.gov.justice.digital.hmpps.oauth2server.integration.specs.pages

class ExistingPasswordChangeEmailErrorPage extends ExistingPasswordPage {

  static at = {
    title == 'Error: HMPPS Digital Services - Change Email Request'
    headingText == 'Change your email'
  }

  static content = {
    errorText { $('#error-detail').text() }
    errorFieldText { $('[data-qa="password-error"]').text() }
  }
}
