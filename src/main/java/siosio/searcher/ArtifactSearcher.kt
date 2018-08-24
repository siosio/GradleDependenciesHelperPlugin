package siosio.searcher

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.completion.impl.*
import com.intellij.codeInsight.lookup.*
import siosio.*

class ArtifactSearcher(override val dependencyText: DependencyText) : CentralSearcher {

    override fun find(resultSet: CompletionResultSet) {
        val text = Client.get(
                "https://search.maven.org/solrsearch/select?q=g:${dependencyText.groupId}" +
                "&rows=200&wt=json")

        resultSet.restartCompletionOnPrefixChange(dependencyText.text.orEmpty())
        resultSet.withRelevanceSorter(
                CompletionSorter.emptySorter().weigh(PreferStartMatching())
        ).addAllElements(ARTIFACT_PATTERN.findAll(text)
                .map {
                    it.groups[1]!!.value
                }
                .distinct()
                .map {
                    LookupElementBuilder.create("${dependencyText.groupId}:$it")
                }.toList())
    }


    companion object {
        private val ARTIFACT_PATTERN = Regex(",\"a\":\"([^\"]+)\"")
    }
}