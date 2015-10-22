package siosio

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral

class OpenMavenCentralProvider : DocumentationProvider {

  companion object {
    private val groupAndArtifact: String = "http://search.maven.org/#search|gav|1|g:\"%s\" AND a:\"%s\""
    private val group: String = "http://search.maven.org/#search|gav|1|g:\"%s\""
  }

  override fun getQuickNavigateInfo(element: PsiElement, element1: PsiElement): String? {
    return null
  }

  override fun getUrlFor(element: PsiElement, element1: PsiElement): List<String>? {
    if (element !is GrLiteral) {
      return null
    }

    val split = Utils.split(Utils.trimQuote(element.text))
    return listOf(
        if (split.size > 2) {
          groupAndArtifact.format(split[0], split[1])
        } else {
          group.format(split[0])
        }
    )
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
