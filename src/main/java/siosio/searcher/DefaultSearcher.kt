package siosio.searcher

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.completion.impl.*
import com.intellij.codeInsight.lookup.*
import siosio.*
import java.awt.SystemColor.*

class DefaultSearcher(override val dependencyText: DependencyText) : CentralSearcher {
    override fun find(resultSet: CompletionResultSet) {
        val result = Client.get("https://search.maven.org/solrsearch/select?q=${dependencyText.text}&rows=100&wt=json")

        resultSet.withRelevanceSorter(
                CompletionSorter.emptySorter().weigh(PreferStartMatching())
        ).addAllElements(ID_PATTERN.findAll(result)
                .mapNotNull {
                    it.groups[1]?.value
                }
                .map {
                    LookupElementBuilder.create(it)
                }
                .toList()
        )
    }


    companion object {
        private val ID_PATTERN = Regex("\\{\"id\":\"([^\"]+)\"")

    }
}