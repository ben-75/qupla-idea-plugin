package org.abra.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ProcessingContext;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AbraCompletionContributor extends CompletionContributor {
    public AbraCompletionContributor() {
//        extend(CompletionType.BASIC,
//                PlatformPatterns.psiElement(AbraTypes.IDENTIFIER).withLanguage(AbraLanguage.INSTANCE),
//                new CompletionProvider<CompletionParameters>() {
//                    public void addCompletions(@NotNull CompletionParameters parameters,
//                                               ProcessingContext context,
//                                               @NotNull CompletionResultSet resultSet) {
//                        Collection<AbraFuncStmt> funcStmts = ((AbraFile)parameters.getOriginalFile()).findAllAccessibleFunc();
//                        for(AbraFuncStmt f:funcStmts){
//                            ItemPresentation itemPresentation = f.getPresentation();
//                            resultSet.addElement(LookupElementBuilder.create(itemPresentation.getPresentableText()).withIcon(itemPresentation.getIcon(true)).appendTailText(itemPresentation.getLocationString(),true));
//                        }
//                        Collection<AbraLutStmt> lutStmts = ((AbraFile)parameters.getOriginalFile()).findAllAccessibleLut();
//                        for(AbraLutStmt l:lutStmts){
//                            ItemPresentation itemPresentation = l.getPresentation();
//                            resultSet.addElement(LookupElementBuilder.create(itemPresentation.getPresentableText()).withIcon(itemPresentation.getIcon(true)).appendTailText(itemPresentation.getLocationString(),true));
//                        }
//                    }
//                }
//        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(AbraLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        PsiElement current = parameters.getOriginalFile().findElementAt(parameters.getOffset());
                        PsiElement parent = current.getParent();
                        if(current instanceof PsiWhiteSpace) {
                            if (parent instanceof AbraTemplateStmt) {
                                resultSet.addElement(LookupElementBuilder.create("func"));
                                resultSet.addElement(LookupElementBuilder.create("type"));
                            }else if (parent instanceof AbraFile) {
                                resultSet.addElement(LookupElementBuilder.create("func"));
                                resultSet.addElement(LookupElementBuilder.create("type"));
                                resultSet.addElement(LookupElementBuilder.create("import"));
                                resultSet.addElement(LookupElementBuilder.create("use"));
                                resultSet.addElement(LookupElementBuilder.create("template"));
                            }else if (parent instanceof AbraFuncBody) {
                                if(((AbraFuncBody) parent).getOpenBrace()!=null){
                                    if(acceptJoinExpr(current)){
                                        resultSet.addElement(LookupElementBuilder.create("join"));
                                        resultSet.addElement(LookupElementBuilder.create("affect"));
                                        resultSet.addElement(LookupElementBuilder.create("state"));
                                    }else if(acceptAffectExpr(current)){
                                        resultSet.addElement(LookupElementBuilder.create("affect"));
                                        resultSet.addElement(LookupElementBuilder.create("state"));
                                    }else if(acceptStateExpr(current)){
                                        resultSet.addElement(LookupElementBuilder.create("state"));
                                    }
                                }
                            }
                        }

                    }
                });
    }

    private boolean acceptJoinExpr(PsiElement current){
        return current.getPrevSibling().getNode().getElementType()==AbraTypes.OPEN_BRACE || current.getPrevSibling() instanceof AbraJoinExpr;
    }
    private boolean acceptAffectExpr(PsiElement current){
        return current.getPrevSibling() instanceof AbraJoinExpr || current.getPrevSibling() instanceof AbraAffectExpr;
    }

    private boolean acceptStateExpr(PsiElement current){
        return current.getPrevSibling() instanceof AbraStateExpr;
    }
}
