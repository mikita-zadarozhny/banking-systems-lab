package org.mikita.bankingsystemslab.transaction.api

import org.mikita.bankingsystemslab.transaction.api.dto.ApiErrorResponseDto
import org.mikita.bankingsystemslab.transaction.api.dto.CreateTransactionRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.CreateTransactionResponseDto
import org.mikita.bankingsystemslab.transaction.exception.AccountDoesNotExistException
import org.mikita.bankingsystemslab.transaction.exception.CurrencyMismatchException
import org.mikita.bankingsystemslab.transaction.exception.NegativeAccountBalanceException
import org.mikita.bankingsystemslab.transaction.service.TransactionService
import org.mikita.bankingsystemslab.transaction.service.command.CreateTransactionCommand
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController (
    private val transactionService: TransactionService
){
    @PostMapping("/api/v1/transactions")
    fun postTransactions(@RequestBody createTransactionDto: CreateTransactionRequestDto):
            ResponseEntity<CreateTransactionResponseDto> {
        val transactionId = transactionService.createTransaction(
            CreateTransactionCommand(
                createTransactionDto.idempotencyKey,
                createTransactionDto.money,
                createTransactionDto.sender,
                createTransactionDto.recipient,
                createTransactionDto.type
            )
        )

        return ResponseEntity(CreateTransactionResponseDto(transactionId), HttpStatus.CREATED);
    }

    @ExceptionHandler(NegativeAccountBalanceException::class)
    fun handleNegativeAccountBalanceException(ex: NegativeAccountBalanceException): ResponseEntity<ApiErrorResponseDto> {
        val error = ApiErrorResponseDto(
            message = "Not sufficient funds."
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(CurrencyMismatchException::class)
    fun handleCurrencyMismatchException(ex: CurrencyMismatchException): ResponseEntity<ApiErrorResponseDto> {
        val error = ApiErrorResponseDto(
            message = "Transaction and Account currency mismatched."
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AccountDoesNotExistException::class)
    fun handleAccountDoesNotExistException(ex: AccountDoesNotExistException): ResponseEntity<ApiErrorResponseDto> {
        val error = ApiErrorResponseDto(
            message = "Cannot operate with non-existing account."
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }
}
