package siosio

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral

class OpenMavenCentralProvider : DocumentationProvider {


  companion object {
    private const val GROUP_AND_ARTIFACT: String = "http://search.maven.org/#search|gav|1|g:\"%s\" AND a:\"%s\""
    private const val GROUP: String = "http://search.maven.org/#search|gav|1|g:\"%s\""
  }

  override fun getQuickNavigateInfo(element: PsiElement?, element1: PsiElement?): String? {
    return null
  }

  override fun getUrlFor(element: PsiElement?, element1: PsiElement?): List<String>? {
    if (element !is GrLiteral) {
      return null
    }

    return split(trimQuote(element.text)).let {
      listOf(
          if (it.size > 2) {
            GROUP_AND_ARTIFACT.format(it[0], it[1])
          } else {
            GROUP.format(it[0])
          }
      )
    }
  }

  override fun generateDoc(element: PsiElement, element1: PsiElement?): String? {
    return null
  }

  override fun getDocumentationElementForLookupItem(manager: PsiManager?, o: Any?, element: PsiElement?): PsiElement? {
    return null
  }

  override fun getDocumentationElementForLink(manager: PsiManager?, s: String?, element: PsiElement?): PsiElement? {
    return null
  }
}
