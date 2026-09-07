package org.mikita.bankingsystemslab.user.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mikita.bankingsystemslab.user.api.dto.ApiErrorResponseDto
import org.mikita.bankingsystemslab.user.api.dto.UserResponseDto
import org.mikita.bankingsystemslab.user.domain.UserStatus
import org.springframework.test.web.servlet.client.expectBody

class UserControllerIntegrationTest : BaseIntegrationTest() {

    @Test
    fun shouldCreateUser_whenNoConflictingUsernameExists() {
        // when & then
        val createdUser = createUser("username_1")
            .expectStatus().isCreated()
            .expectBody<UserResponseDto>().consumeWith {
                val user = it.responseBody
                assertNotNull(user)
                assertEquals("username_1", user.username)
                assertEquals(UserStatus.ENABLED, user.status)
            }.returnResult().responseBody!!

        getUser(createdUser.userId)
            .expectStatus().isOk()
            .expectBody<UserResponseDto>().consumeWith {
                val user = it.responseBody
                assertNotNull(user)
                assertEquals(createdUser.userId, user.userId)
                assertEquals(createdUser.username, user.username)
                assertEquals(createdUser.status, user.status)
            }.returnResult().responseBody!!
    }

    @Test
    fun shouldReturn409HttpStatusCode_whenConflictingUsernameExists() {
        // when & then
        createUser("username_2")
            .expectStatus().isCreated()
            .expectBody<UserResponseDto>().consumeWith {
                val user = it.responseBody
                assertNotNull(user)
                assertEquals("username_2", user.username)
                assertEquals(UserStatus.ENABLED, user.status)
            }.returnResult().responseBody!!

        createUser("username_2")
            .expectStatus().isEqualTo(409)
            .expectBody<ApiErrorResponseDto>().consumeWith {
                val error = it.responseBody
                assertNotNull(error)
                assertEquals("Username is already taken.", error.message)
            }.returnResult().responseBody!!
    }
}
