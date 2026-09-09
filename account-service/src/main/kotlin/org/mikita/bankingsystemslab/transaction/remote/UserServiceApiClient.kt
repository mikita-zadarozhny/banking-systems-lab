package org.mikita.bankingsystemslab.transaction.remote

import org.mikita.bankingsystemslab.transaction.domain.user.User
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class UserServiceApiClient (
    private val restClient: RestClient
) {

    fun getUser(id: Long): User {
        return restClient.get()
            .uri("/api/v1/users/{userId}", id)
            .retrieve()
            .body(User::class.java)!!
    }
}
