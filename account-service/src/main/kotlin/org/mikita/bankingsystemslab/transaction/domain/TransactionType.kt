package org.mikita.bankingsystemslab.transaction.domain

import java.util.HashMap

enum class TransactionType (val code: Int) {
    TRANSFER(100),
    DEPOSIT(101),
    WITHDRAW(102);

    companion object {

        private val codeToTransactionType: HashMap<Int, TransactionType> = HashMap()

        init {
            codeToTransactionType.put(TRANSFER.code, TRANSFER)
            codeToTransactionType.put(DEPOSIT.code, DEPOSIT)
            codeToTransactionType.put(WITHDRAW.code, WITHDRAW)
        }

        fun ofCode(code: Int): TransactionType {
            return codeToTransactionType.get(code)!!
        }
    }
}
