package org.abra.language;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.abra.language.psi.AbraTypes;
import org.jetbrains.annotations.NotNull;

public class AbraFindUsagesProvider implements FindUsagesProvider {
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    public String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    public String getType(@NotNull PsiElement element) {
        return "";
    }

    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
        return ((PsiNamedElement)element).getName();
    }

    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }

    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new AbraLexerAdapter(), TokenSet.create(AbraTypes.IDENTIFIER), TokenSet.create(AbraTypes.COMMENT), TokenSet.create(AbraTypes.INTEGER, AbraTypes.TRIT));
    }
}
