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
import java.util.regex.Pattern

public class GradleDependenciesCompletionContributor : AbstractGradleCompletionContributor() {

  init {
    extend(CompletionType.SMART,
        psiElement(javaClass<PsiElement>())
            .and(AbstractGradleCompletionContributor.GRADLE_FILE_PATTERN)
            .withParent(javaClass<GrLiteral>())
            .withSuperParent(5, psiElement(javaClass<GrMethodCallExpression>())
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
        val text = trimQuote(it.getText())
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

    val splitPattern = Pattern.compile(":")

    fun isShortText(text: String?) = (text?.length() ?: 0) < 2

    fun trimQuote(text: String) = text.trim('"', '\'').trimEnd('"', '\'')
  }
}

