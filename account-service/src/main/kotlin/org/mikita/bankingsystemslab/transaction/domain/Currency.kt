package org.mikita.bankingsystemslab.transaction.domain

import java.util.HashMap

enum class Currency (val code: Int) {
    USD (1000),
    EUR (1001);

    companion object {

        private val codeToCurrency: HashMap<Int, Currency> = HashMap()

        init {
            codeToCurrency.put(USD.code, USD)
            codeToCurrency.put(EUR.code, EUR)
        }

        fun ofCode(code: Int): Currency {
            return codeToCurrency.get(code)!!
        }
    }
}
