package siosio

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral

class OpenMavenCentralProvider : DocumentationProvider {

  companion object {
    //private val detailPageUrl: String = "http://search.maven.org/#artifactdetails|%s|%s|%s|jar"
    private val mavenUrl: String = "http://search.maven.org/#search|gav|1|g:\"%s\" AND a:\"%s\""
  }

  override fun getQuickNavigateInfo(element: PsiElement, element1: PsiElement): String? {
    return null
  }

  override fun getUrlFor(element: PsiElement, element1: PsiElement): List<String>? {
    if (element !is GrLiteral) {
      return null
    }

    val searchParam = SearchParam(Utils.trimQuote(element.text))
    if (!MavenFinder().contains(searchParam)) {
      return null
    }

    return listOf(mavenUrl.format(searchParam.group, searchParam.name))
  }

  override fun generateDoc(element: PsiElement, element1: PsiElement?): String? {
    return null
  }

  override fun getDocumentationElementForLookupItem(manager: PsiManager, o: Any, element: PsiElement): PsiElement? {
    return null
  }

  override fun getDocumentationElementForLink(manager: PsiManager, s: String, element: PsiElement): PsiElement? {
    return null
  }
}
