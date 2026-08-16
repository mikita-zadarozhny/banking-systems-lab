package org.mikita.bankingsystemslab.transaction.domain

import java.util.HashMap

enum class AccountType (val code: Int) {
    CUSTOMER_DEPOSIT (10),
    CASH (11),
    RESERVE (12);

    companion object {

        private val codeToAccountType: HashMap<Int, AccountType> = HashMap()

        init {
            codeToAccountType.put(CUSTOMER_DEPOSIT.code, CUSTOMER_DEPOSIT)
            codeToAccountType.put(CASH.code, CASH)
            codeToAccountType.put(RESERVE.code, RESERVE)
        }

        fun ofCode(code: Int): AccountType {
            return codeToAccountType.get(code)!!
        }
    }
}