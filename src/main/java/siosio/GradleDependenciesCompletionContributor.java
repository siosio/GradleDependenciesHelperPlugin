package siosio;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.codeInsight.AbstractGradleCompletionContributor;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression;

public class GradleDependenciesCompletionContributor extends AbstractGradleCompletionContributor {

    public GradleDependenciesCompletionContributor() {
        extend(CompletionType.SMART,
                psiElement(PsiElement.class).and(GRADLE_FILE_PATTERN)
                        .withParent(GrLiteral.class)
                        .withSuperParent(5, psiElement(GrMethodCallExpression.class).withText(string().contains("dependencies"))),
                new CompletionParametersCompletionProvider());
    }

    private static class CompletionParametersCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet set) {

            PsiElement position = parameters.getOriginalPosition();
            if (position == null) {
                return;
            }
            String text = trimQuote(position.getText());
            if (StringUtil.isEmpty(text) || text.length() < 2) {
                return;
            }
            String[] split = text.split(":");
            Set<String> result;
            if (split.length == 3 || split.length == 2) {
                set.restartCompletionOnPrefixChange(text);
                result = new MavenFindAction().findVersion(split[0], split[1]);
            } else {
                result = new MavenFindAction().find(text);
            }

            List<LookupElement> elements = new ArrayList<LookupElement>();
            for (String str : result) {
                elements.add(LookupElementBuilder.create(str));
            }
            set.addAllElements(elements);
            set.stopHere();
        }

        private String trimQuote(String text) {
            if (text.startsWith("'") || text.startsWith("\"")) {
                text = text.substring(1);
            }
            if (text.endsWith("'") || text.endsWith("\"")) {
                text = text.substring(0, text.length() - 1);
            }
            return text;
        }
    }
}

