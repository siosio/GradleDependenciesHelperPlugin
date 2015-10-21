package siosio

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.string
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.jetbrains.plugins.gradle.codeInsight.AbstractGradleCompletionContributor
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression

public class GradleDependenciesCompletionContributor : AbstractGradleCompletionContributor() {

  init {
    extend(CompletionType.SMART,
        psiElement(PsiElement::class.java)
            .and(AbstractGradleCompletionContributor.GRADLE_FILE_PATTERN)
            .withParent(GrLiteral::class.java)
            .withSuperParent(5, psiElement(GrMethodCallExpression::class.java)
                .withText(string().contains("dependencies"))), CompletionParametersCompletionProvider())
  }

  private class CompletionParametersCompletionProvider : CompletionProvider<CompletionParameters>() {

    val mavenFinder = MavenFinder()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet) {

      val position = parameters.getOriginalPosition()

      position?.let {
        val text = Utils.trimQuote(it.getText())
        if (isShortText(text)) {
          return
        }

        val searchParam = SearchParam(text)

        if (searchParam.isFindVersion()) {
          resultSet.restartCompletionOnPrefixChange(text)
        }

        resultSet.addAllElements(
            mavenFinder.find(searchParam)
                .map {
                  LookupElementBuilder.create(it)
                }.toList())
        resultSet.stopHere()
      }
    }
  }

  companion object {

    fun isShortText(text: String?) = (text?.length() ?: 0) < 2

  }
}

