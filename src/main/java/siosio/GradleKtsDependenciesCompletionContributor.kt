package siosio

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns.*
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.plugins.gradle.codeInsight.AbstractGradleCompletionContributor

class GradleKtsDependenciesCompletionContributor : CompletionContributor() {
    init {
        val containsDependencies = string().contains("dependencies")
        extend(CompletionType.SMART,
                psiElement(KtTokens.REGULAR_STRING_PART)
                        .and(psiElement().inFile(psiFile().withName(StandardPatterns.string().endsWith(".gradle.kts"))))
                        .and(
                                psiElement()
                                        .andOr(
                                                psiElement().withSuperParent(10, psiElement(KtCallExpression::class.java).withText(containsDependencies)),
                                                psiElement().withSuperParent(13, psiElement(KtCallExpression::class.java).withText(containsDependencies))
                                        )
                        ),
                DependenciesCompletionProvider())
    }
}