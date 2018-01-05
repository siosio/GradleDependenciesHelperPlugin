package siosio

import com.intellij.codeInsight.completion.*

interface CentralSearcher {
    
    val dependencyText: DependencyText

    fun find(resultSet: CompletionResultSet)
}