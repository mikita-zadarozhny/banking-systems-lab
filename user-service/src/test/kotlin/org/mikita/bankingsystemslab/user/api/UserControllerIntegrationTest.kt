package org.mikita.bankingsystemslab.user.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mikita.bankingsystemslab.user.api.dto.UserResponseDto
import org.mikita.bankingsystemslab.user.domain.UserStatus
import org.springframework.test.web.servlet.client.expectBody

class UserControllerIntegrationTest : BaseIntegrationTest() {

    @Test
    fun shouldCreateUser() {
        // when & then
        createUser("username_1")
            .expectStatus().isCreated()
            .expectBody<UserResponseDto>().consumeWith {
                val user = it.responseBody
                assertNotNull(user)
                assertEquals("username_1", user.username)
                assertEquals(UserStatus.ENABLED, user.status)
            }.returnResult().responseBody!!
    }
}
