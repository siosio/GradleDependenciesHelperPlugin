package siosio.searcher

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.util.*
import siosio.*

class VersionSearcher(override val dependencyText: DependencyText) : CentralSearcher {

    override fun find(resultSet: CompletionResultSet) {
        val text = Client.get(
                "https://search.maven.org/solrsearch/select?q=g:${dependencyText.groupId}+AND+a:${dependencyText.artifactId}" +
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
                    .withInsertHandler { context, _ ->
                        val editor = context.editor
                        val document = editor.document
                        val offset = editor.caretModel.offset
                        val char = document.getText(TextRange(offset, offset + 1))
                        if (char != "$") {
                            return@withInsertHandler
                        }
                        val allText = document.text
                        val indexOf = allText.indexOf('"', offset)
                        if (document.getLineNumber(indexOf) != document.getLineNumber(offset)) {
                            return@withInsertHandler
                        }
                        document.deleteString(offset, indexOf)
                    }
        })
    }

    companion object {
        private val VERSION_PATTERN = Regex("\\{\"id\":\"([^\"]+)\"")

        class VersionComparable(private val index: Int) : Comparable<VersionComparable> {
            override fun compareTo(other: VersionComparable): Int = this.index - other.index
        }
    }
}