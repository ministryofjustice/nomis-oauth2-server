package uk.gov.justice.digital.hmpps.oauth2server.auth.repository

import org.springframework.data.repository.CrudRepository
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.Group

interface GroupRepository : CrudRepository<Group, String> {

  fun findAllByOrderByGroupName(): List<Group>
  fun findByGroupCode(groupCode: String?): Group?
}
