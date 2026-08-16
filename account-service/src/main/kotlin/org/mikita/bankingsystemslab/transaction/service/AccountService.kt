package org.mikita.bankingsystemslab.transaction.service

import org.mikita.bankingsystemslab.transaction.domain.Account
import org.mikita.bankingsystemslab.transaction.exception.AccountDoesNotExistException
import org.mikita.bankingsystemslab.transaction.exception.NegativeAccountBalanceException
import org.mikita.bankingsystemslab.transaction.repository.AccountRepository
import org.mikita.bankingsystemslab.transaction.service.command.CreateAccountCommand
import org.mikita.bankingsystemslab.transaction.service.command.UpdateBalanceCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService (
    private val accountRepository: AccountRepository
){
    @Transactional
    fun updateBalance(updateBalanceCommand: UpdateBalanceCommand) {
        val optionalAccount  = accountRepository.findById(updateBalanceCommand.accountId)
        if(optionalAccount.isEmpty) {
            throw AccountDoesNotExistException(updateBalanceCommand.accountId)
        }
        val account = optionalAccount.get()
        val updatedAccount = account.copy(balance = account.balance + updateBalanceCommand.delta)

        if(updatedAccount.balance < 0) {
            throw NegativeAccountBalanceException(
                updateBalanceCommand.accountId,
                account.balance.amount,
                updateBalanceCommand.delta.amount
            )
        }

        accountRepository.updateBalanceOptimistically(updatedAccount)
    }

    @Transactional
    fun createAccount(createAccountCommand: CreateAccountCommand): Long {
        return accountRepository.createAccount(createAccountCommand.currency)
    }
}
