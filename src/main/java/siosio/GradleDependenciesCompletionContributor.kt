package siosio

import com.intellij.codeInsight.completion.*
import com.intellij.patterns.*
import com.intellij.patterns.PlatformPatterns.*
import com.intellij.patterns.StandardPatterns.string
import com.intellij.psi.*
import com.intellij.util.*
import org.jetbrains.plugins.gradle.codeInsight.*
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.*
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.*

class GradleDependenciesCompletionContributor : AbstractGradleCompletionContributor() {

    init {
        val containsDependencies = string().contains("dependencies")
        extend(CompletionType.SMART,
                psiElement(PsiElement::class.java)
                        .and(psiElement().inFile(PlatformPatterns.psiFile().withName(StandardPatterns.string().endsWith(".gradle"))))
                        .andOr(
                                psiElement()
                                        .withParent(GrLiteral::class.java)
                                        .andOr(
                                                psiElement().withSuperParent(5, psiElement(GrMethodCallExpression::class.java).withText(containsDependencies)),
                                                psiElement().withSuperParent(7, psiElement(GrMethodCallExpression::class.java).withText(containsDependencies))
                                        ),
                                psiElement()
                                        .withParent(GrStringContent::class.java)
                                        .withSuperParent(6, psiElement(GrMethodCallExpression::class.java)
                                                .withText(containsDependencies))
                        
                        ), DependenciesCompletionProvider())
    }

}

