package siosio.searcher

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.*
import siosio.*

class VersionSearcher(override val dependencyText: DependencyText) : CentralSearcher {

    override fun find(resultSet: CompletionResultSet) {
        val text = Client.get(
                "https://search.maven.org/solrsearch/select?q=g:${dependencyText.getGroupId()}+AND+a:${dependencyText.getArtifactId()}" +
                "&rows=100&core=gav&wt=json")


        val versions = VERSION_PATTERN.findAll(text).mapNotNull {
            it.groups[1]?.value
        }.distinct().toList()

        resultSet.restartCompletionOnPrefixChange(dependencyText.text)
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
    }

    companion object {
        private val VERSION_PATTERN = Regex("\\{\"id\":\"([^\"]+)\"")

        class VersionComparable(private val index: Int) : Comparable<VersionComparable> {
            override fun compareTo(other: VersionComparable): Int = this.index - other.index
        }
    }
}