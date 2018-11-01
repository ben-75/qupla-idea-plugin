package org.abra.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.abra.language.psi.AbraFile;
import org.abra.language.psi.AbraImportStmt;
import org.abra.language.psi.AbraPsiImplUtil;
import org.jetbrains.annotations.NotNull;

public abstract class ReferencesCache extends ASTWrapperPsiElement implements AbraImportStmt {

    public ReferencesCache(@NotNull ASTNode node) {
        super(node);
    }

    private final Object stateLock = new Object();
    private PsiReference[] references;
    @NotNull
    public PsiReference[] getReferences() {
        if(references==null) {
            synchronized (stateLock){
                references = AbraPsiImplUtil.getReferences(this);
            }
        }
        return references;
//    return AbraPsiImplUtil.getReferences(this);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        synchronized (stateLock){
            references = null;
            ((AbraFile)getNode().getPsi().getContainingFile()).invalidateCache();
        }
//        if(getProject().getUserData(ImportCache.KEY)!=null) {
//            getProject().getUserData(ImportCache.KEY).invalidate();
//        }
    }
}
