@file:Suppress("DEPRECATION")

package uk.gov.justice.digital.hmpps.oauth2server.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationService
import org.springframework.security.oauth2.provider.NoSuchClientException
import org.springframework.security.oauth2.provider.client.BaseClientDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.Client
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.ClientDeployment
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.ClientDeploymentRepository
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.ClientRepository
import uk.gov.justice.digital.hmpps.oauth2server.security.PasswordGenerator

@Service
class ClientService(
  private val clientsDetailsService: ClientDetailsService,
  private val clientRegistrationService: ClientRegistrationService,
  private val passwordGenerator: PasswordGenerator,
  private val clientRepository: ClientRepository,
  private val clientDeploymentRepository: ClientDeploymentRepository,
) {

  @Throws(ClientAlreadyExistsException::class)
  fun addClient(clientDetails: ClientDetails): String {
    clientRegistrationService.addClientDetails(clientDetails)
    val clientSecret = passwordGenerator.generatePassword()
    clientRegistrationService.updateClientSecret(clientDetails.clientId, clientSecret)
    return clientSecret
  }

  fun findAndUpdateDuplicates(clientId: String) {
    val clientDetails = clientsDetailsService.loadClientByClientId(clientId)
    find(clientId).filter { it.id != clientId }
      .map { copyClient(it.id, clientDetails as BaseClientDetails) }
      .forEach { clientRegistrationService.updateClientDetails(it) }
  }

  fun listUniqueClients(): List<Client> =
    clientRepository.findAll().sortedBy { it.id }.distinctBy { baseClientId(it.id) }

  fun loadClientWithCopies(clientId: String): ClientDetailsWithCopies {
    val clients = find(clientId)
    return ClientDetailsWithCopies(clientsDetailsService.loadClientByClientId(clientId), clients)
  }

  fun loadClientAndDeployment(clientId: String): ClientDuplicateIdsAndDeployment {

    val clientDetailId = clientsDetailsService.loadClientByClientId(clientId).clientId

    val clientIds = find(clientId).map { it.id }

    return ClientDuplicateIdsAndDeployment(
      clientDetailId, clientIds, loadClientDeploymentDetails(clientId)
    )
  }

  private fun find(clientId: String): List<Client> {
    val searchClientId = baseClientId(clientId)
    return clientRepository.findByIdStartsWithOrderById(searchClientId)
      .filter { it.id == searchClientId || it.id.substringAfter(searchClientId).matches(clientIdSuffixRegex) }
  }

  fun loadClientDeploymentDetails(clientId: String): ClientDeployment? {
    val searchClientId = baseClientId(clientId)
    return clientDeploymentRepository.findByIdOrNull(searchClientId)
  }

  fun getClientDeploymentDetailsAndBaseClientId(clientId: String): Pair<ClientDeployment?, String> {
    val baseClientId = baseClientId(clientId)
    return Pair(clientDeploymentRepository.findByIdOrNull(baseClientId), baseClientId)
  }

  @Transactional(transactionManager = "authTransactionManager")
  fun saveClientDeploymentDetails(clientDeployment: ClientDeployment) {
    clientDeploymentRepository.save(clientDeployment)
  }

  @Transactional(transactionManager = "authTransactionManager")
  @Throws(NoSuchClientException::class)
  fun generateClientSecret(clientId: String): String {
    val clientSecret = passwordGenerator.generatePassword()
    clientRegistrationService.updateClientSecret(clientId, clientSecret)
    clientRepository.findByIdOrNull(clientId)?.resetSecretUpdated()
    return clientSecret
  }

  @Transactional(transactionManager = "authTransactionManager")
  @Throws(NoSuchClientException::class)
  fun removeClient(clientId: String) {
    val clients = find(clientId)
    if (clients.size == 1) {
      val baseClientId = baseClientId(clientId)
      clientDeploymentRepository.deleteByBaseClientId(baseClientId)
    }
    clientRegistrationService.removeClientDetails(clientId)
  }

  private fun copyClient(clientId: String, clientDetails: BaseClientDetails): BaseClientDetails {
    val client = BaseClientDetails(clientDetails)
    client.clientId = clientId
    // copy constructor doesn't copy all the fields over so need to copy the extra ones
    client.additionalInformation = clientDetails.additionalInformation
    client.setAutoApproveScopes(clientDetails.autoApproveScopes)
    return client
  }

  @Throws(DuplicateClientsException::class)
  fun duplicateClient(clientId: String): ClientDetails {
    val clientDetails = clientsDetailsService.loadClientByClientId(clientId)
    val duplicateClientDetails = copyClient(incrementClientId(clientId), clientDetails as BaseClientDetails)
    duplicateClientDetails.clientSecret = passwordGenerator.generatePassword()
    clientRegistrationService.addClientDetails(duplicateClientDetails)
    return duplicateClientDetails
  }

  @Throws(DuplicateClientsException::class)
  private fun incrementClientId(clientId: String): String {
    val clients = find(clientId)
    if (clients.size > 2) {
      throw DuplicateClientsException(clientId, "MaxReached")
    }

    val baseClientId = baseClientId(clientId)
    val ids = clients.map {
      clientNumber(it.id)
    }

    val increment = ids.maxOrNull()?.plus(1)

    return "$baseClientId-$increment"
  }

  private fun baseClientId(clientId: String): String = clientId.replace(regex = clientIdSuffixRegex, replacement = "")
  private fun clientNumber(clientId: String): Int = clientId.substringAfterLast("-").toIntOrNull() ?: 0

  private val clientIdSuffixRegex = "-[0-9]*$".toRegex()
}

data class ClientDetailsWithCopies(val clientDetails: ClientDetails, val duplicates: List<Client>)
data class ClientDuplicateIdsAndDeployment(
  val requestedClientId: String,
  val duplicates: List<String>,
  val clientDeployment: ClientDeployment?
)

open class DuplicateClientsException(clientId: String, errorCode: String) :
  Exception("Duplicate clientId failed for $clientId with reason: $errorCode")
