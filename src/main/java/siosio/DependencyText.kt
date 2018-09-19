package siosio

import com.intellij.codeInsight.completion.*
import siosio.searcher.*

class DependencyText(val text: String?) {

    val isShort: Boolean = text?.length ?: 0 < 2
    
    private val splitText = text?.split(":") ?: emptyList()
    val groupId = splitText.getOrElse(0) { "" }
    val artifactId = splitText.getOrElse(1) { "" }

    fun addCompletions(resultSet: CompletionResultSet) {
        when (splitText.size) {
            3 -> VersionSearcher(this)
            2 -> ArtifactSearcher(this)
            else -> DefaultSearcher(this)
        }.find(resultSet)
    }

}