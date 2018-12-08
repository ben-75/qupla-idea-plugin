package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbraVarOrParamPsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public AbraVarOrParamPsiReferenceImpl(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        AbraParamOrVarNameRef ref = AbraElementFactory.createAbraVarOrParamNameRef(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return resolveLocally();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PsiElement funcBody = myElement;
        PsiElement myExpression = null;
        while(!(funcBody instanceof AbraFuncBody)){
            if(funcBody.getParent() instanceof AbraFuncBody){
                myExpression = funcBody;
            }
            funcBody = funcBody.getParent();
        }
        List<PsiElement> allRefs = new ArrayList<>();

        //look in state vars
        if(((AbraFuncBody)funcBody).getStateExprList()!=null) {
            for (AbraStateExpr stateExpr : ((AbraFuncBody) funcBody).getStateExprList()){
                if(stateExpr.equals(myExpression))break;
                allRefs.add(stateExpr.getVarName());
            }
        }

        //look in local vars
        if(((AbraFuncBody)funcBody).getAssignExprList()!=null) {
            for (AbraAssignExpr assignExpr : ((AbraFuncBody) funcBody).getAssignExprList()){
                if(assignExpr.equals(myExpression))break;
                allRefs.add(assignExpr.getVarName());

            }
        }
        //look in function parameters
        for(AbraFuncParameter p:((AbraFuncBody)funcBody).getFuncSignature().getFuncParameterList()){
            allRefs.add(p.getParamName());
        }
        return allRefs.toArray();
    }

    private PsiElement resolveLocally(){
        PsiElement funcBody = myElement;
        PsiElement myExpression = null;
        while(!(funcBody instanceof AbraFuncBody)){
            if(funcBody.getParent() instanceof AbraFuncBody){
                myExpression = funcBody;
            }
            funcBody = funcBody.getParent();
        }

        //look in state vars
        if(((AbraFuncBody)funcBody).getStateExprList()!=null) {
            for (AbraStateExpr stateExpr : ((AbraFuncBody) funcBody).getStateExprList()){
                if(stateExpr.equals(myExpression))break;
                if(stateExpr.getVarName().getText().equals(myElement.getText())){
                    return stateExpr.getVarName();
                }
            }
        }

        //look in local vars
        if(((AbraFuncBody)funcBody).getAssignExprList()!=null) {
            for (AbraAssignExpr assignExpr : ((AbraFuncBody) funcBody).getAssignExprList()){
                if(assignExpr.equals(myExpression))break;
                if(assignExpr.getVarName().getText().equals(myElement.getText())){
                    return assignExpr.getVarName();
                }
            }
        }
        //look in function parameters
        for(AbraFuncParameter p:((AbraFuncBody)funcBody).getFuncSignature().getFuncParameterList()){
            if(p.getParamName().getText().equals(myElement.getText())){
                return p.getParamName();
            }
        }

        //look in template types
        if(funcBody.getParent().getParent() instanceof AbraTemplateStmt){
            AbraTemplateStmt templateStmt = (AbraTemplateStmt)funcBody.getParent().getParent();
            for(AbraPlaceHolderTypeName phn:templateStmt.getPlaceHolderTypeNameList()){
                if(phn.getText().equals(myElement.getText())){
                    return phn;
                }
            }
            for(AbraTypeStmt phn:templateStmt.getTypeStmtList()){
                if(phn.getTypeName().getText().equals(myElement.getText())){
                    return phn;
                }
            }
        }
        return null;
    }

}
