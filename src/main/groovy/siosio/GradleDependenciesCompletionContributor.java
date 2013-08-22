package siosio;

import java.util.List;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GradleDependenciesCompletionContributor extends CompletionContributor {

    public GradleDependenciesCompletionContributor() {
        extend(CompletionType.BASIC,
                psiElement(PsiElement.class)
                        .withSuperParent(3, psiElement(GrApplicationStatement.class))
                        .withParent(GrLiteral.class),
                new CompletionParametersCompletionProvider());
    }

    private static class CompletionParametersCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(
                @NotNull CompletionParameters parameters,
                ProcessingContext context,
                @NotNull CompletionResultSet set) {

            if (!parameters.getOriginalFile().getName().endsWith(".gradle")) {
                return;
            }

            PsiElement position = parameters.getOriginalPosition();
            if (position == null) {
                return;
            }
            String text = trimQuote(position.getText());
            if (StringUtil.isEmpty(text) || text.length() < 5) {
                return;
            }
            List<String> result = new MavenFindAction().find(text);
            for (final String id : result) {
                set.addElement(new LookupElement() {
                    @NotNull
                    @Override
                    public String getLookupString() {
                        return id;
                    }
                });
            }
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
