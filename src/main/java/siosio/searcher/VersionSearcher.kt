package siosio.searcher

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionSorter
import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementWeigher
import siosio.CentralSearcher
import siosio.Client
import siosio.DependencyText

class VersionSearcher(override val dependencyText: DependencyText) : CentralSearcher {

    override fun find(resultSet: CompletionResultSet) {
        val text = Client.get(
                "https://search.maven.org/solrsearch/select?q=g:${dependencyText.groupId}+AND+a:${dependencyText.artifactId}" +
                "&rows=100&core=gav&wt=json")

        val versions = VERSION_PATTERN.findAll(text).mapNotNull {
            it.groups[1]?.value
        }.distinct().toList()

        resultSet.withPrefixMatcher(PrefixMatcher.ALWAYS_TRUE)
        val withRelevanceSorter = resultSet.withRelevanceSorter(
                CompletionSorter.emptySorter().weigh(object : LookupElementWeigher("gradleDependencyWeigher") {
                    override fun weigh(element: LookupElement): Comparable<VersionComparable> {
                        return VersionComparable(versions.indexOf(element.lookupString))
                    }
                })
        )
        withRelevanceSorter.addAllElements(versions.map {
            LookupElementBuilder.create(it)
        })
        resultSet.stopHere()
    }

    companion object {
        private val VERSION_PATTERN = Regex("\\{\"id\":\"([^\"]+)\"")

        class VersionComparable(private val index: Int) : Comparable<VersionComparable> {
            override fun compareTo(other: VersionComparable): Int = this.index - other.index
        }
    }
}