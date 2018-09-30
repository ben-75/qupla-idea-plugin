package org.abra.interpreter;

import org.abra.interpreter.action.ConcatTermEvaluator;
import org.abra.language.psi.AbraPostfixExpr;
import org.abra.utils.TRIT;

public class PostFixEvaluator {

    //postfixExpr     ::= mergeExpr  |  concatExpr | simpleExpr

    public static TRIT[] eval(AbraPostfixExpr expr, AbraEvaluationContext context){
        if(expr.getConcatExpr()!=null)return ConcatEvaluator.eval(expr.getConcatExpr(), context);
        if(expr.getConcatTerm()!=null)return ConcatTermEvaluator.eval(expr.getConcatTerm(), context);
        if(expr.getMergeExpr()!=null)return MergeEvaluator.eval(expr.getMergeExpr(), context);
        throw new AbraSyntaxError(InterpreterUtils.getErrorLocationString(expr));
    }
}
