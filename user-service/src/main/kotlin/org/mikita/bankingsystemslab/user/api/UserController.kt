package org.mikita.bankingsystemslab.user.api

import org.mikita.bankingsystemslab.user.api.dto.CreateUserRequestDto
import org.mikita.bankingsystemslab.user.api.dto.UserResponseDto
import org.mikita.bankingsystemslab.user.service.UserService
import org.mikita.bankingsystemslab.user.service.command.CreateUserCommand
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController (
    private val userService: UserService
) {

    @PostMapping
    fun postUser(@RequestBody createUserRequestDto: CreateUserRequestDto) : ResponseEntity<UserResponseDto> {

        val user = userService.createUser(
            CreateUserCommand(
                username = createUserRequestDto.username
            )
        )

        return ResponseEntity<Void>.status(HttpStatus.CREATED)
            .body(UserResponseDto(
                userId = user.userId!!,
                username = user.username,
                status = user.status
            ))
    }

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: Long) : ResponseEntity<UserResponseDto> {

        val user = userService.getUser(userId)

        return ResponseEntity<UserResponseDto>.ok(
            UserResponseDto(
                userId = user.userId!!,
                username = user.username,
                status = user.status
            )
        )
    }

}
