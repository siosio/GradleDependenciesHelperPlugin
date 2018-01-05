package siosio

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.completion.impl.*
import com.intellij.codeInsight.lookup.*
import com.intellij.patterns.*
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.string
import com.intellij.psi.*
import com.intellij.util.*
import org.jetbrains.plugins.gradle.codeInsight.*
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.*
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.*

class GradleDependenciesCompletionContributor : AbstractGradleCompletionContributor() {

    init {
        extend(CompletionType.SMART,
                psiElement(PsiElement::class.java)
                        .and(psiElement().inFile(PlatformPatterns.psiFile().withName(StandardPatterns.string().endsWith(".gradle"))))
                        .withParent(GrLiteral::class.java)
                        .withSuperParent(5, psiElement(GrMethodCallExpression::class.java)
                                .withText(string().contains("dependencies"))), CompletionParametersCompletionProvider())
    }

    private class CompletionParametersCompletionProvider : CompletionProvider<CompletionParameters>() {

        override fun addCompletions(
                parameters: CompletionParameters,
                context: ProcessingContext,
                resultSet: CompletionResultSet) {

            val position = parameters.originalPosition ?: return

            DependencyText(trimQuote(position.text))
                    .takeUnless { it.isShort }
                    ?.addCompletions(resultSet)
        }
    }
}

