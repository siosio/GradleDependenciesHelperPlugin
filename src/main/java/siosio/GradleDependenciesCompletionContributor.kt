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

    val mavenFinder = MavenFinder()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet) {

      val position = parameters.originalPosition

      position?.let {
        val text = trimQuote(it.text)
        if (isShortText(text)) {
          return
        }

        val searchParam = SearchParam(text)

        val searchResult = mavenFinder.find(searchParam)
        val completionResultSet = if (searchParam.isFindVersion()) {
          resultSet.restartCompletionOnPrefixChange(text)
          resultSet.withRelevanceSorter(
              CompletionSorter.emptySorter().weigh(object : LookupElementWeigher("gradleDependencyWeigher") {
                override fun weigh(element: LookupElement): Comparable<*> {
                  return DependencyComparable(searchResult, element)
                }
              })
          )
        } else {
          resultSet.withRelevanceSorter(
              CompletionSorter.emptySorter().weigh(PreferStartMatching())
          )
        }

        searchResult.forEach {
          completionResultSet.addElement(LookupElementBuilder.create(it))
        }
      }
    }
  }

  class DependencyComparable(
      versions: Set<String>,
      private val element: LookupElement) : Comparable<DependencyComparable> {

    private val index: Int;

    init {
      index = versions.indexOf(element.lookupString)
    }

    override fun compareTo(other: DependencyComparable): Int = this.index - other.index
  }

  companion object {
    fun isShortText(text: String?) = (text?.length ?: 0) < 2
  }
}

