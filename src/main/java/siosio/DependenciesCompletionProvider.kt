package siosio

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext

class DependenciesCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            resultSet: CompletionResultSet) {

        val originalElement = parameters.originalPosition ?: return
        if (originalElement.isValid.not()) {
            return
        }
        DependencyText(trimQuote(originalElement.text))
                .takeUnless { it.isShort }
                ?.addCompletions(resultSet)
    }
}
