package org.abra.interpreter;

import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.abra.utils.TRIT;

public class FuncEvaluator {

    public static TRIT[] eval(AbraFuncExpr expr, AbraEvaluationContext context){
        System.out.println("FuncEvaluator:"+expr.getText());
        PsiElement resolved = expr.getFuncNameRef().getReference().resolve();
        if(resolved==null){
            throw new AbraSyntaxError("Cannot resolve "+expr.getFuncNameRef()+" at "+InterpreterUtils.getErrorLocationString(expr));
        }
        AbraFuncDefinition funcDefinition = null;
        AbraEvaluationContext newContext = new AbraEvaluationContext();
        if(resolved instanceof AbraTemplateNameRef){
            AbraUseStmt useStmt = (AbraUseStmt) resolved.getParent();
            PsiElement templateName = resolved.getReference().resolve();
            if(! (templateName instanceof AbraTemplateName)){
                throw new AbraSyntaxError("Cannot resolve template "+useStmt.getTemplateNameRef().getText()+" at "+InterpreterUtils.getErrorLocationString(useStmt.getTemplateNameRef()));
            }
            AbraTemplateStmt templateStmt = (AbraTemplateStmt) templateName.getParent();
            int i=0;
            for(AbraPlaceHolderName phn:templateStmt.getPlaceHolderNameList()){
                AbraTypeName typeName = (AbraTypeName) useStmt.getTypeNameRefList().get(i).getReference().resolve();
                if(typeName==null){
                    throw new AbraSyntaxError("Cannot resolve type "+useStmt.getTypeNameRefList().get(i).getText()+" at "+InterpreterUtils.getErrorLocationString(useStmt.getTypeNameRefList().get(i)));
                }
                AbraTypeStmt typeStmt = (AbraTypeStmt) typeName.getParent();
                newContext.add(phn,typeStmt.getResolvedSize());
                i++;
            }
            funcDefinition = templateStmt.getFuncDefinition();
        }else {
            funcDefinition = (AbraFuncDefinition) resolved.getParent();
        }

            int i=0;
            for(AbraFuncParameter param:funcDefinition.getFuncParameterList()){
                TRIT[] paramValue = PostFixEvaluator.eval(expr.getPostfixExprList().get(i), context);
                newContext.add(param.getParamName(),paramValue);
                i++;
            }

        context.pushChildContext(newContext);
        try {
            return execute(context, funcDefinition, newContext);
        }finally {
            context.popContext();
        }
    }

    public static TRIT[] execute(AbraEvaluationContext rootContext, AbraFuncDefinition funcDefinition, AbraEvaluationContext newContext) {
        for(AbraAssignExpr assignExpr:funcDefinition.getFuncBody().getAssignExprList()){
            newContext.add(assignExpr.getVarName(), ReturnEvaluator.eval(assignExpr.getReturnExpr(),rootContext));
        }


            return ReturnEvaluator.eval(funcDefinition.getFuncBody().getReturnExpr(), rootContext);

    }
}
