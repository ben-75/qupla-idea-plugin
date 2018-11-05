package org.abra.interpreter.action;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.BaseProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import org.abra.interpreter.runconfig.AbraInterpreterRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraInterpreterProgramRunner extends DefaultJavaProgramRunner {

    @Override
    protected void execute(@NotNull ExecutionEnvironment environment, @Nullable Callback callback, @NotNull RunProfileState state) throws ExecutionException {
           System.out.println("runner execute");
        //state.execute(environment.getExecutor(),this);
        super.execute(environment, callback, state);
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "org.abra.interpreter.java";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return /*executorId.equals("Run")*/ super.canRun(executorId, profile) && profile instanceof AbraInterpreterRunConfiguration;
    }
}
