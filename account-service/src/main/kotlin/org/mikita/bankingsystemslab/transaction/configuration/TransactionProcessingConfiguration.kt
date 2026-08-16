package org.mikita.bankingsystemslab.transaction.configuration

import org.mikita.bankingsystemslab.transaction.service.processor.TransactionProcessor
import org.mikita.bankingsystemslab.transaction.service.processor.TransactionProcessorContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.stream.Collectors

@Configuration
class TransactionProcessingConfiguration {

    @Bean
    fun transactionProcessorContainer(
        transactionProcessors: List<TransactionProcessor>
    ): TransactionProcessorContainer {

        val map = transactionProcessors.stream().collect(
                Collectors.toMap(
                    { it: TransactionProcessor -> it.getType() },
                    { it: TransactionProcessor -> it })
            )

        return TransactionProcessorContainer(map)
    }

}
