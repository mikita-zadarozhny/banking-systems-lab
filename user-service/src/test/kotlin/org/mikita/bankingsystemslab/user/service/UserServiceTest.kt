package org.mikita.bankingsystemslab.user.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mikita.bankingsystemslab.user.domain.User
import org.mikita.bankingsystemslab.user.domain.UserStatus
import org.mikita.bankingsystemslab.user.exception.UserDoesNotExistException
import org.mikita.bankingsystemslab.user.repository.UserRepository
import org.mikita.bankingsystemslab.user.service.command.CreateUserCommand
import java.util.Optional

class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    lateinit var userService: UserService

    @BeforeEach
    fun beforeEach() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        userService = UserService(userRepository)
    }

    @Test
    fun shouldCreateUser() {

        // given
        val createUserCommand = CreateUserCommand(
            "username_1"
        )

        val expected = User(
            userId = 100L,
            username = "username_1",
            status = UserStatus.ENABLED
        )

        every { userRepository.saveNew(User(
            userId = null,
            username = "username_1",
            status = UserStatus.ENABLED
        )) } returns expected

        // when
        val actual = userService.createUser(createUserCommand)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun shouldGetUser_whenUserExists() {

        // given
        val expected = User(
            userId = 100L,
            username = "username_1",
            status = UserStatus.ENABLED
        )

        every { userRepository.findById(100) } returns Optional.of(expected)

        // when
        val actual = userService.getUser(100)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun shouldThrowException_whenUserDoesNotExist() {

        // given
        every { userRepository.findById(100) } returns Optional.empty()

        // when
        val actual = assertThrows<UserDoesNotExistException> {
            userService.getUser(100)
        }

        // then
        assertEquals("User with ID '100' does not exist", actual.message)
    }
}
