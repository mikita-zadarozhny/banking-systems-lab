package org.mikita.bankingsystemslab.transaction.api

import org.mikita.bankingsystemslab.transaction.api.dto.CreateAccountRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.CreateAccountResponseDto
import org.mikita.bankingsystemslab.transaction.service.AccountService
import org.mikita.bankingsystemslab.transaction.service.command.CreateAccountCommand
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController (
    private val accountService: AccountService
) {

    @PostMapping("/api/v1/accounts")
    fun postAccounts(@RequestBody createAccountDto: CreateAccountRequestDto): ResponseEntity<CreateAccountResponseDto> {
        val accountId = accountService.createAccount(
            CreateAccountCommand(
                createAccountDto.currency
            )
        )

        return ResponseEntity(CreateAccountResponseDto(accountId), HttpStatus.CREATED)
    }
}
