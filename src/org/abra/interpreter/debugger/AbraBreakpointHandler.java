package org.abra.interpreter.debugger;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaBreakpointHandler;
import com.intellij.debugger.engine.events.DebuggerCommandImpl;
import com.intellij.debugger.impl.DebuggerManagerImpl;
import com.intellij.debugger.impl.PrioritizedTask;
import com.intellij.debugger.ui.breakpoints.*;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.impl.breakpoints.LineBreakpointState;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointManagerImpl;
import com.intellij.xdebugger.impl.breakpoints.XLineBreakpointImpl;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.LocatableEvent;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static org.abra.interpreter.debugger.AbraPositionManager.isEvaluable;

public class AbraBreakpointHandler extends JavaBreakpointHandler {

    private final AbraToJavaMapper abraToJavaMapper;

    public AbraBreakpointHandler(DebugProcessImpl process, AbraToJavaMapper abraToJavaMapper) {
//        super(AbraBreakpointType.class, process);
        super(AbraBreakpointType.class, process);
        this.abraToJavaMapper = abraToJavaMapper;
    }

    @Nullable
    protected Breakpoint createJavaBreakpoint(@NotNull XBreakpoint xBreakpoint) {
        PsiElement target = getPsiElement((XLineBreakpointImpl) xBreakpoint);
        if(target==null)return null;
        AbraToJavaMapper.JavaBreakpointInfo javaMethod = abraToJavaMapper.getEvalMethod(target);
        if(javaMethod==null)return null;
        LineBreakpointState state = new LineBreakpointState();
        state.setTemporary(false);
        state.setFileUrl(javaMethod.getMethod().getContainingFile().getVirtualFile().getUrl());
        state.setLine(javaMethod.getLineNumber());
        XLineBreakpointImpl javaBreakpoint = new XLineBreakpointImpl(
                new JavaLineBreakpointType(),
                (XBreakpointManagerImpl) (XDebuggerManager.getInstance(((XLineBreakpointImpl) xBreakpoint).getProject())).getBreakpointManager(),
                null,
                state);
        LineBreakpoint lineBreakpoint = LineBreakpoint.create(((XLineBreakpointImpl) xBreakpoint).getProject(), javaBreakpoint);



        javaBreakpoint.putUserData(Breakpoint.DATA_KEY, lineBreakpoint);
        return lineBreakpoint;
    }

    @Override
    public void registerBreakpoint(@NotNull XBreakpoint breakpoint) {
//        BreakpointManager.addBreakpoint(createJavaBreakpoint2(breakpoint));
//        //breakpoint.myXBreakpoint.getUserData(Breakpoint.DATA_KEY) == breakpoint
//        breakpoint.myXBreakpoint.setUserData(Breakpoint.DATA_KEY,breakpoint);

        PsiElement psiElement = AbraPositionManager.getAbraPsiElement(
                PsiManager.getInstance(((XLineBreakpointImpl) breakpoint).getProject()).findFile(breakpoint.getSourcePosition().getFile()),
                breakpoint.getSourcePosition().getOffset());
        if(psiElement!=null){
            setCondition(psiElement,breakpoint);
        }

        AbraLineBreakpoint abraLineBreakpoint = new AbraLineBreakpoint(((XLineBreakpointImpl) breakpoint).getProject(),breakpoint);
        breakpoint.putUserData(Breakpoint.DATA_KEY, abraLineBreakpoint);
        //Breakpoint javaBreakpoint = BreakpointManager.getJavaBreakpoint(breakpoint);
//        Breakpoint javaBreakpoint = BreakpointManager.getJavaBreakpoint(breakpoint);
//        if (javaBreakpoint == null) {
//            javaBreakpoint = createJavaBreakpoint(breakpoint);
//            breakpoint.putUserData(Breakpoint.DATA_KEY, javaBreakpoint);
//        }
        if (abraLineBreakpoint != null) {
            final Breakpoint bpt = abraLineBreakpoint;
            BreakpointManager.addBreakpoint(bpt);
            // use schedule not to block initBreakpoints
            DebuggerCommandImpl cmd = new DebuggerCommandImpl(){
                @Override
                protected void action() throws Exception {
                    bpt.createRequest(myProcess);
                }

                @Override
                public Priority getPriority() {
                    return PrioritizedTask.Priority.HIGH;
                }
            };
            myProcess.getManagerThread().schedule(cmd);
        }
    }

    @Override
    public void unregisterBreakpoint(@NotNull XBreakpoint breakpoint, boolean temporary) {
        final Breakpoint javaBreakpoint = breakpoint.getUserData(Breakpoint.DATA_KEY);;
        if (javaBreakpoint != null) {
            // use schedule not to block initBreakpoints
            DebuggerCommandImpl cmd = new DebuggerCommandImpl() {
                @Override
                protected void action() throws Exception {
                    myProcess.getRequestsManager().deleteRequest(javaBreakpoint);
                }

                @Override
                public Priority getPriority() {
                    return PrioritizedTask.Priority.HIGH;
                }
            };
            myProcess.getManagerThread().schedule(cmd);
        }
    }

    private void setCondition(PsiElement element, XBreakpoint breakpoint){
        if(element instanceof AbraReturnExpr){
            element = ((AbraReturnExpr)element).getMergeExpr();
            while(element.getChildren().length==1 && isEvaluable(element.getChildren()[0])){
                element = element.getChildren()[0];
            }
        }
        String newCond = null;
        if(element instanceof AbraAssignExpr){
            newCond = "(assign.module.pathName.endsWith(\""+breakpoint.getSourcePosition().getFile().getName()+"\") && assign.origin.lineNr=="+breakpoint.getSourcePosition().getLine()+")";
        }else if(element instanceof AbraConcatExpr){
            newCond = "(exprs.get(0).module.pathName.endsWith(\""+breakpoint.getSourcePosition().getFile().getName()+"\") && exprs.get(0).origin.lineNr=="+breakpoint.getSourcePosition().getLine()+")";
        }else if(element instanceof AbraLutExpr){
            newCond = "(lookup.module.pathName.endsWith(\""+breakpoint.getSourcePosition().getFile().getName()+"\") && lookup.origin.lineNr=="+breakpoint.getSourcePosition().getLine()+")";
        }else if(element instanceof AbraMergeExpr){
            newCond = "(merge.module.pathName.endsWith(\""+breakpoint.getSourcePosition().getFile().getName()+"\") && merge.origin.lineNr=="+breakpoint.getSourcePosition().getLine()+")";
        }else if(element instanceof AbraSliceExpr){
            newCond = "(slice.module.pathName.endsWith(\""+breakpoint.getSourcePosition().getFile().getName()+"\") && slice.origin.lineNr=="+breakpoint.getSourcePosition().getLine()+")";
        }else if(element instanceof AbraInteger){
            newCond = "(integer.module.pathName.endsWith(\""+breakpoint.getSourcePosition().getFile().getName()+"\") && integer.origin.lineNr=="+breakpoint.getSourcePosition().getLine()+")";
        }

        if(newCond!=null) {
            String cond0 = breakpoint.getCondition();
            if(cond0==null || cond0.length()==0){
                breakpoint.setCondition(newCond);
            }else{
                if(cond0.indexOf(newCond)==-1){
                    breakpoint.setCondition(cond0 + "||" + newCond);
                }
            }
        }else{
            breakpoint.setCondition("false");
        }

    }


    private PsiElement getPsiElement(XLineBreakpointImpl breakpoint){
        PsiElement element = XDebuggerUtil.getInstance().findContextElement(breakpoint.getFile(),breakpoint.getSourcePosition().getOffset(),breakpoint.getProject(),false);
        while(element instanceof PsiWhiteSpace && element!=null)element = element.getNextSibling();
        if(element==null)return null;
        while(!(element instanceof AbraFuncExpr) && !(element instanceof AbraConcatExpr)
                && !(element instanceof AbraAssignExpr) && !(element instanceof AbraLutExpr) && !(element instanceof AbraMergeExpr) && !(element instanceof AbraSliceExpr)
                && !(element instanceof AbraStateExpr)&& !(element instanceof AbraFile))element = element.getParent();
        if(element instanceof AbraFile) return null;
        return element;
    }
}