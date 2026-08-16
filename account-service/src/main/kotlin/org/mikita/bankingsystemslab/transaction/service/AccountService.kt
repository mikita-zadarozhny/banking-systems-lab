package org.mikita.bankingsystemslab.transaction.service

import org.mikita.bankingsystemslab.transaction.domain.Account
import org.mikita.bankingsystemslab.transaction.exception.AccountDoesNotExistException
import org.mikita.bankingsystemslab.transaction.exception.NegativeAccountBalanceException
import org.mikita.bankingsystemslab.transaction.repository.AccountRepository
import org.mikita.bankingsystemslab.transaction.service.command.CreateAccountCommand
import org.mikita.bankingsystemslab.transaction.service.command.UpdateAccountBalanceCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService (
    private val accountRepository: AccountRepository
){
    @Transactional
    fun updateAccountBalance(updateAccountBalanceCommand: UpdateAccountBalanceCommand) {
        val optionalAccount  = accountRepository.findById(updateAccountBalanceCommand.accountId)
        if(optionalAccount.isEmpty) {
            throw AccountDoesNotExistException(updateAccountBalanceCommand.accountId)
        }
        val account = optionalAccount.get()
        val updatedAccount = account.copy(balance = account.balance + updateAccountBalanceCommand.delta)

        if(updatedAccount.balance < 0) {
            throw NegativeAccountBalanceException(
                updateAccountBalanceCommand.accountId,
                account.balance.amount,
                updateAccountBalanceCommand.delta.amount
            )
        }

        accountRepository.updateBalanceOptimistically(updatedAccount)
    }

    @Transactional
    fun createAccount(createAccountCommand: CreateAccountCommand): Account {
        return accountRepository.saveNew(createAccountCommand.currency)
    }

    @Transactional
    fun getAccount(accountId: Long): Account {
        return accountRepository.getById(accountId)
    }
}
