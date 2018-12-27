package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbraTemplatePsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public AbraTemplatePsiReferenceImpl(@NotNull AbraTemplateNameRef element) {
        super(element);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Override
    public AbraTemplateNameRef handleElementRename(String newElementName) throws IncorrectOperationException {
        AbraTemplateNameRef ref = AbraElementFactory.createAbraTemplateNameRef(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement resolved = resolveInFile(myElement.getContainingFile());
        if(resolved==null){
            resolved = resolveFromImports(myElement.getContainingFile());
        }
        return resolved;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private AbraTemplateName resolveInFile(PsiFile aFile){
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(AbraTypes.TEMPLATE_STMT))){
            if(((AbraTemplateStmt)stmt.getPsi()).getTemplateName().getText().equals(myElement.getText())){
                return ((AbraTemplateStmt)stmt.getPsi()).getTemplateName();
            }
        }
        return null;
    }

//    private AbraTemplateName resolveFromImports(PsiFile startingFile){
//        for(ASTNode stmt:startingFile.getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
//            PsiReference[] importedFiles = AbraPsiImplUtil.getReferences((AbraImportStmt) stmt.getPsi());
//            if(importedFiles!=null) {
//                for (PsiReference psiRef : importedFiles) {
//                    PsiElement anAbraFile = psiRef.resolve();
//                    if(anAbraFile!=null){
//                        AbraTemplateName resolved = resolveInFile((PsiFile) anAbraFile);
//                        if(resolved!=null)return resolved;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    private PsiElement resolveFromImports(PsiFile startingFile){
        List<AbraFile> importsTree = (((AbraFile)startingFile).getImportTree());
        return resolveFromImportTree(importsTree);
    }

    public PsiElement resolveFromImportTree(List<AbraFile> scope){
        if(scope.size()>0){
            for(PsiFile f:scope){
                PsiElement resolved = resolveInFile(f);
                if(resolved!=null)return resolved;
            }
        }
        return null;
    }
}
