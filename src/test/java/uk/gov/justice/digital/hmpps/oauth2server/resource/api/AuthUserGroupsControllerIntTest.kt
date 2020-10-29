package uk.gov.justice.digital.hmpps.oauth2server.resource.api

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.oauth2server.resource.IntegrationTest

class AuthUserGroupsControllerIntTest : IntegrationTest() {

  private val invalidToken =
    "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJlZGYzOWQwMy03YmJkLTQ2ZGYtOTQ5Ny1mYzI2MDg2ZWIzYTgiLCJzdWIiOiJJVEFHX1VTRVJfQURNIiwidXNlcl9uYW1lIjoiSVRBR19VU0VSX0FETSIsImNsaWVudF9pZCI6ImVsaXRlMmFwaWNsaWVudCIsImF1dGhvcml0aWVzIjpbXSwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sImV4cCI6MTYwMzM2NjY0N30.Vi4z77ylpS94ztVyEQoilkRuMDDDfvYVPblQRmUA5ACo3TF4-9NW2xE1Hm4hURwesayMs_apBrW2iAbPVtiTRC_TiMFApPXU-SoMadO5QcqKumXx_z2HfV_J_1eQKS0RJBxaz89xdeR2ilTTEmUyk38IulFJ0IVY2k65gCkQffKn6uE3K4NDBATQXbBwQZ7Soqr89fmsh-xym9JCA63AB_aU42S39sWl7OtUildrf9UgNv81rnOSs1eLDFdcmgztUSdac2hyX01u0vai51biz93-IBF5xdIdAInDmNktF9jwrYsindDu3LCiubrqGuK3MScZDB7A_OW5gHSfyCHmvw"

  @Test
  fun `Auth User Groups add group endpoint adds a group to a user`() {

    webTestClient
      .get().uri("/auth/api/authuser/AUTH_RO_USER/groups")
      .headers(setAuthorisation("ITAG_USER_ADM"))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath(".[?(@.groupCode == 'SITE_1_GROUP_2')]")
      .doesNotExist()

    webTestClient
      .put().uri("/auth/api/authuser/AUTH_RO_USER/groups/site_1_group_2")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .get().uri("/auth/api/authuser/AUTH_RO_USER/groups")
      .headers(setAuthorisation("ITAG_USER_ADM"))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath(".[?(@.groupCode == 'SITE_1_GROUP_2')]")
      .isEqualTo(mapOf("groupCode" to "SITE_1_GROUP_2", "groupName" to "Site 1 - Group 2"))
  }

  @Test
  fun `Auth User Groups remove group endpoint removes a group from a user`() {
    webTestClient
      .get().uri("/auth/api/authuser/AUTH_RO_USER/groups")
      .headers(setAuthorisation("ITAG_USER_ADM"))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath(".[?(@.groupCode == 'SITE_1_GROUP_1')]")
      .isEqualTo(mapOf("groupCode" to "SITE_1_GROUP_1", "groupName" to "Site 1 - Group 1"))

    webTestClient
      .delete().uri("/auth/api/authuser/AUTH_RO_USER/groups/site_1_group_1")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .get().uri("/auth/api/authuser/AUTH_RO_USER/groups")
      .headers(setAuthorisation("ITAG_USER_ADM"))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath(".[?(@.groupCode == 'SITE_1_GROUP_1')]")
      .doesNotExist()
  }

  @Test
  fun `Auth User Groups endpoint returns user groups`() {
    webTestClient
      .get().uri("/auth/api/authuser/auth_ro_vary_user/groups")
      .headers(setAuthorisation("ITAG_USER_ADM"))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .json(
        """
       [{"groupCode":"SITE_1_GROUP_1","groupName":"Site 1 - Group 1"},{"groupCode":"SITE_1_GROUP_2","groupName":"Site 1 - Group 2"}]
        """.trimIndent()
      )
  }

  @Test
  fun `Auth User Groups endpoint not accessible without valid token`() {
    webTestClient
      .get().uri("/auth/api/authuser/auth_ro_vary_user/groups")
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `Auth User Groups add group endpoint not accessible without valid token`() {
    webTestClient
      .put().uri("/auth/api/authuser/auth_ro_vary_user/groups/licence_ro")
      .header("Authorization", "Basic $invalidToken")
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `Auth User Groups remove group endpoint not accessible without valid token`() {
    webTestClient
      .delete().uri("/auth/api/authuser/auth_ro_vary_user/groups/licence_ro")
      .header("Authorization", "Basic $invalidToken")
      .exchange()
      .expectStatus().isUnauthorized
  }
}