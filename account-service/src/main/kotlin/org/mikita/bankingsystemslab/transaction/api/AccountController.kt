package org.mikita.bankingsystemslab.transaction.api

import org.mikita.bankingsystemslab.transaction.api.dto.CreateAccountRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.AccountResponseDto
import org.mikita.bankingsystemslab.transaction.api.dto.ApiErrorResponseDto
import org.mikita.bankingsystemslab.transaction.exception.AccountDoesNotExistException
import org.mikita.bankingsystemslab.transaction.service.AccountService
import org.mikita.bankingsystemslab.transaction.service.command.CreateAccountCommand
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController (
    private val accountService: AccountService
) {

    @PostMapping("/api/v1/accounts")
    fun postAccounts(@RequestBody createAccountDto: CreateAccountRequestDto): ResponseEntity<AccountResponseDto> {
        val account = accountService.createAccount(
            CreateAccountCommand(
                createAccountDto.currency
            )
        )

        return ResponseEntity(AccountResponseDto(
            accountId = account.accountId,
            balance = account.balance
        ), HttpStatus.CREATED)
    }

    @GetMapping("/api/v1/accounts/{accountId}")
    fun getAccount(@PathVariable("accountId") accountId: Long): ResponseEntity<AccountResponseDto> {
        val account = accountService.getAccount(accountId)

        return ResponseEntity(AccountResponseDto(
            accountId = account.accountId,
            balance = account.balance
        ), HttpStatus.OK)
    }

    @ExceptionHandler(AccountDoesNotExistException::class)
    fun handleAccountDoesNotExistException(ex: AccountDoesNotExistException): ResponseEntity<ApiErrorResponseDto> {
        val error = ApiErrorResponseDto(
            message = "Account does not exist."
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }
}
