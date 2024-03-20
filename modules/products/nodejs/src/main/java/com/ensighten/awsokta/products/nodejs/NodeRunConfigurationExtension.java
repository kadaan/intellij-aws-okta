package com.ensighten.awsokta.products.nodejs;

import com.ensighten.awsokta.platform.ui.RunConfigSettingsEditor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.javascript.nodejs.execution.AbstractNodeTargetRunProfile;
import com.intellij.javascript.nodejs.execution.runConfiguration.AbstractNodeRunConfigurationExtension;
import com.intellij.openapi.options.SettingsEditor;
import com.ensighten.awsokta.platform.AwsOktaEnvironmentVariables;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class NodeRunConfigurationExtension extends AbstractNodeRunConfigurationExtension {

    @Nullable
    @Override
    public String getEditorTitle() {
        return RunConfigSettingsEditor.getEditorTitle();
    }

    @Override
    protected void patchCommandLine(@NotNull AbstractNodeTargetRunProfile nodeRunConfigurationBase,
                                    @Nullable RunnerSettings runnerSettings,
                                    @NotNull GeneralCommandLine generalCommandLine,
                                    @NotNull String runnerId,
                                    @NotNull Executor executor
    )
            throws ExecutionException
    {
        Map<String, String> newEnv = new AwsOktaEnvironmentVariables(
                RunConfigSettingsEditor.getAwsOktaSettings(nodeRunConfigurationBase)
        )
                .render(
                        nodeRunConfigurationBase.getProject(),
                        generalCommandLine.getEnvironment(),
                        generalCommandLine.isPassParentEnvironment()
                );

        if (newEnv == null) {
            return;
        }

        generalCommandLine.getEnvironment().clear();
        generalCommandLine.getEnvironment().putAll(newEnv);
    }

    @Override
    protected void validateConfiguration(@NotNull AbstractNodeTargetRunProfile configuration, boolean isExecution) throws Exception {
        RunConfigSettingsEditor.validateConfiguration(configuration, isExecution);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return RunConfigSettingsEditor.getSerializationId();
    }

    @Override
    protected void readExternal(@NotNull AbstractNodeTargetRunProfile runConfiguration, @NotNull Element element) {
        RunConfigSettingsEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void writeExternal(@NotNull AbstractNodeTargetRunProfile runConfiguration, @NotNull Element element) {
        RunConfigSettingsEditor.writeExternal(runConfiguration, element);
    }

    @NotNull
    @Override
    public <P extends AbstractNodeTargetRunProfile> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new RunConfigSettingsEditor<>(configuration);
    }

    @Override
    public boolean isApplicableFor(@NotNull AbstractNodeTargetRunProfile nodeRunConfigurationBase) {
        return true;
    }
}
