package org.mikita.bankingsystemslab.user.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
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
    fun shouldSaveNew_whenNoConflictingUserDoesNotExist() {

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

    @Test
    fun shouldFindById_whenUserExists() {

        // given
        val user = User(
            userId = null,
            username = "username_1",
            status = UserStatus.ENABLED
        )

        userRepository.saveNew(user)

        val expectedUserId: Long = getCurrentUserIdSequenceValue()

        val expected = User(
            userId = expectedUserId,
            username = "username_1",
            status = UserStatus.ENABLED
        )

        // when
        val actual = userRepository.findById(expectedUserId)

        // then
        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun shouldReturnEmptyOptional_whenFindById_andUserDoesNotExist() {

        // given
        val userId = 123L

        // when
        val actual = userRepository.findById(userId)

        // then
        assertTrue(actual.isEmpty)
    }
}
