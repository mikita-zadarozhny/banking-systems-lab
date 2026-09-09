package org.mikita.bankingsystemslab.transaction.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient

class UserServiceApiClientTest {

    lateinit var mockServer: MockRestServiceServer

    lateinit var restClient: RestClient

    lateinit var userServiceApiClient: UserServiceApiClient

    @BeforeEach
    fun beforeEach() {
        val builder = RestClient.builder()

        mockServer = MockRestServiceServer.bindTo(builder).build()
        restClient = builder.build()
        userServiceApiClient = UserServiceApiClient(restClient)
    }

    @Test
    fun shouldGetUser_whenUserServiceRespondsWithUser() {

        // given
        val userId = 100L

        mockServer.expect(requestTo("/api/v1/users/100"))
            .andExpect(method(org.springframework.http.HttpMethod.GET))
            .andRespond(withSuccess("{\"username\":\"username_1\"}", MediaType.APPLICATION_JSON))

        // when
        val user = userServiceApiClient.getUser(userId)

        // then
        assertEquals("username_1", user.username)
    }

}
