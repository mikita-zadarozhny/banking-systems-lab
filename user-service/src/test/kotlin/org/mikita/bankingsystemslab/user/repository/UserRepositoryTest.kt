package org.mikita.bankingsystemslab.user.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mikita.bankingsystemslab.user.domain.User
import org.mikita.bankingsystemslab.user.domain.UserStatus
import org.mikita.bankingsystemslab.user.exception.UsernameClashException

class UserRepositoryTest : BaseJdbcTest() {

    private lateinit var userRepository: UserRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        userRepository = UserRepository(jdbcTemplate)
    }

    @Test
    fun saveNew_whenNoConflictingUserDoesNotExist() {

        // given
        val user = User(
            userId = null,
            username = "username_1",
            status = UserStatus.ENABLED
        )

        val expected = User(
            userId = getCurrentUserIdSequenceValue(),
            username = "username_1",
            status = UserStatus.ENABLED
        )

        // when
        val actual = userRepository.saveNew(user)

        // then
        assertEquals(expected, actual)
    }

    @Test
    @Tag("Not Implemented")
    fun shouldThrowException_whenSaveNew_andConflictingUsernameExists() {

        // given
        val user = User(
            userId = null,
            username = "username_1",
            status = UserStatus.ENABLED
        )

        val conflictingUser = User(
            userId = null,
            username = "username_1",
            status = UserStatus.ENABLED
        )

        userRepository.saveNew(user)

        // when
        val exception = assertThrows<UsernameClashException> {
            userRepository.saveNew(conflictingUser)
        }

        // then
        assertEquals("Username 'username_1' already exists", exception.message)
    }
}
