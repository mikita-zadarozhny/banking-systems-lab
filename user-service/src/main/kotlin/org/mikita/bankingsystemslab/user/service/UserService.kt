package org.mikita.bankingsystemslab.user.service

import org.mikita.bankingsystemslab.user.domain.User
import org.mikita.bankingsystemslab.user.domain.UserStatus
import org.mikita.bankingsystemslab.user.exception.UserDoesNotExistException
import org.mikita.bankingsystemslab.user.repository.UserRepository
import org.mikita.bankingsystemslab.user.service.command.CreateUserCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository
){

    @Transactional
    fun createUser(createUserCommand: CreateUserCommand): User {
        val user = User(
            userId = null,
            username = createUserCommand.username,
            status = UserStatus.ENABLED
        )

        return userRepository.saveNew(user)
    }

    @Transactional
    fun getUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserDoesNotExistException(userId) }
    }
}
