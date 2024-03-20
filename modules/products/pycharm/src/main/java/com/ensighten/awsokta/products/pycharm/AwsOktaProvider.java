package com.ensighten.awsokta.products.pycharm;

import com.ensighten.awsokta.platform.ui.RunConfigSettingsEditor;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.PythonExecution;
import com.jetbrains.python.run.PythonRunParams;
import com.jetbrains.python.run.target.HelpersAwareTargetEnvironmentRequest;
import com.jetbrains.python.run.target.PythonCommandLineTargetEnvironmentProvider;
import com.ensighten.awsokta.platform.AwsOktaEnvironmentVariables;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AwsOktaProvider implements PythonCommandLineTargetEnvironmentProvider {
    @Override
    public void extendTargetEnvironment(
            @NotNull Project project,
            @NotNull HelpersAwareTargetEnvironmentRequest helpersAwareTargetEnvironmentRequest,
            @NotNull PythonExecution pythonExecution,
            @NotNull PythonRunParams pythonRunParams
    ) {
        if (pythonRunParams instanceof AbstractPythonRunConfiguration<?>) {
            try {
                final AbstractPythonRunConfiguration<?> runConfiguration = (AbstractPythonRunConfiguration<?>) pythonRunParams;

                Map<String, String> newEnv = new AwsOktaEnvironmentVariables(
                        RunConfigSettingsEditor.getAwsOktaSettings(runConfiguration)
                )
                        .render(
                                runConfiguration.getProject(),
                                pythonRunParams.getEnvs(),
                                pythonRunParams.isPassParentEnvs()
                        );

                if (newEnv != null) {
                    newEnv.forEach(pythonExecution::addEnvironmentVariable);
                }

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
