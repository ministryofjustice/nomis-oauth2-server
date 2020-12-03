package uk.gov.justice.digital.hmpps.oauth2server.resource.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.hmpps.oauth2server.resource.IntegrationTest

class AuthUserControllerIntTest : IntegrationTest() {

  @Test
  fun `Auth User Enable endpoint enables user`() {
    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/enable")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .get().uri("/auth/api/authuser/AUTH_STATUS")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$").value<Map<String, Any>> {
        assertThat(it).containsAllEntriesOf(
          mapOf(
            "userId" to "fc494152-f9ad-48a0-a87c-9adc8bd75255",
            "username" to "AUTH_STATUS",
            "email" to null,
            "firstName" to "Auth",
            "lastName" to "Status",
            "locked" to false,
            "enabled" to true,
            "verified" to true,
          )
        )
      }
  }

  @Test
  fun `Group manager Enable endpoint enables user`() {
    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS2/groups/site_1_group_2")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS2/enable")
      .headers(setAuthorisation("AUTH_GROUP_MANAGER", listOf("ROLE_AUTH_GROUP_MANAGER")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .get().uri("/auth/api/authuser/AUTH_STATUS2")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$").value<Map<String, Any>> {
        assertThat(it).containsAllEntriesOf(
          mapOf(
            "userId" to "fc494152-f9ad-48a0-a87c-9adc8bd75266",
            "username" to "AUTH_STATUS2",
            "email" to null,
            "firstName" to "Auth",
            "lastName" to "Status2",
            "locked" to false,
            "enabled" to true,
            "verified" to true,
          )
        )
      }
  }

  @Test
  fun `Group manager Enable endpoint fails user not in group manager group forbidden`() {
    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/enable")
      .headers(setAuthorisation("AUTH_GROUP_MANAGER", listOf("ROLE_AUTH_GROUP_MANAGER")))
      .exchange()
      .expectStatus().isForbidden
      .expectBody()
      .json(
        """
      {"error":"unable to maintain user","error_description":"Unable to enable user, the user is not within one of your groups","field":"groups"}
        """.trimIndent()
      )
  }

  @Test
  fun `Auth User Enable endpoint fails is not an admin user`() {
    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/enable")
      .headers(setAuthorisation("ITAG_USER", listOf()))
      .exchange()
      .expectStatus().isForbidden
      .expectBody()
      .json(
        """
      {"error":"access_denied","error_description":"Access is denied"}
        """.trimIndent()
      )
  }

  @Test
  fun `Auth User Disable endpoint disables user`() {
    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/disable")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .get().uri("/auth/api/authuser/AUTH_STATUS")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$").value<Map<String, Any>> {
        assertThat(it).containsAllEntriesOf(
          mapOf(
            "userId" to "fc494152-f9ad-48a0-a87c-9adc8bd75255",
            "username" to "AUTH_STATUS",
            "email" to null,
            "firstName" to "Auth",
            "lastName" to "Status",
            "locked" to false,
            "enabled" to false,
            "verified" to true,
          )
        )
      }
  }

  @Test
  fun `Group manager Disable endpoint enables user`() {
    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/groups/site_1_group_2")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/enable")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/disable")
      .headers(setAuthorisation("AUTH_GROUP_MANAGER", listOf("ROLE_AUTH_GROUP_MANAGER")))
      .exchange()
      .expectStatus().isNoContent

    webTestClient
      .get().uri("/auth/api/authuser/AUTH_STATUS")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$").value<Map<String, Any>> {
        assertThat(it).containsAllEntriesOf(
          mapOf(
            "userId" to "fc494152-f9ad-48a0-a87c-9adc8bd75255",
            "username" to "AUTH_STATUS",
            "email" to null,
            "firstName" to "Auth",
            "lastName" to "Status",
            "locked" to false,
            "enabled" to false,
            "verified" to true,
          )
        )
      }
  }

  @Test
  fun `Group manager Disable endpoint fails user not in group manager group forbidden`() {
    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/disable")
      .headers(setAuthorisation("AUTH_GROUP_MANAGER", listOf("ROLE_AUTH_GROUP_MANAGER")))
      .exchange()
      .expectStatus().isForbidden
      .expectBody()
      .json(
        """
      {"error":"unable to maintain user","error_description":"Unable to disable user, the user is not within one of your groups","field":"groups"}
        """.trimIndent()
      )
  }

  @Test
  fun `Auth User Disable endpoint fails is not an admin user`() {
    webTestClient
      .put().uri("/auth/api/authuser/AUTH_STATUS/disable")
      .headers(setAuthorisation("ITAG_USER", listOf()))
      .exchange()
      .expectStatus().isForbidden
      .expectBody()
      .json(
        """
      {"error":"access_denied","error_description":"Access is denied"}
        """.trimIndent()
      )
  }

  @Test
  fun `Amend User endpoint succeeds to alter user email`() {
    webTestClient
      .post().uri("/auth/api/authuser/AUTH_NEW_USER")
      .body(BodyInserters.fromValue(mapOf("email" to "bobby.b@digital.justice.gov.uk")))
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isOk

    webTestClient
      .get().uri("/auth/api/authuser/AUTH_NEW_USER")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$").value<Map<String, Any>> {
        assertThat(it).containsAllEntriesOf(
          mapOf(
            "userId" to "67a789de-7d29-4863-b9c2-f2ce715dc4bc",
            "username" to "AUTH_NEW_USER",
            "email" to "bobby.b@digital.justice.gov.uk",
            "firstName" to "Auth",
            "lastName" to "New-User",
            "locked" to false,
            "enabled" to true,
            "verified" to false,
          )
        )
      }
  }

  @Test
  fun `Amend User endpoint fails if no privilege`() {
    webTestClient
      .post().uri("/auth/api/authuser/AUTH_NEW_USER")
      .body(BodyInserters.fromValue(mapOf("email" to "bobby.b@digital.justice.gov.uk")))
      .headers(setAuthorisation("ITAG_USER_ADM"))
      .exchange()
      .expectStatus().isForbidden
      .expectBody()
      .json(
        """
      {"error":"access_denied","error_description":"Access is denied"}
        """.trimIndent()
      )
  }

  @Test
  fun `Auth User Assignable Groups endpoint for normal user returns their own groups`() {
    webTestClient
      .get().uri("/auth/api/authuser/me/assignable-groups")
      .headers(setAuthorisation("AUTH_RO_VARY_USER", listOf("ROLE_AUTH_RO_VARY_USER")))
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
  fun `Auth User Assignable Groups endpoint for super user returns all groups`() {
    webTestClient
      .get().uri("/auth/api/authuser/me/assignable-groups")
      .headers(setAuthorisation("ITAG_USER_ADM", listOf("ROLE_MAINTAIN_OAUTH_USERS")))
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.[*].groupCode").value<List<String>> {
        assertThat(it).hasSizeGreaterThan(5)
        assertThat(it).contains("SITE_1_GROUP_1")
        assertThat(it).contains("SITE_1_GROUP_2")
        assertThat(it).contains("SITE_2_GROUP_1")
        assertThat(it).contains("SITE_3_GROUP_1")
      }
  }
}
