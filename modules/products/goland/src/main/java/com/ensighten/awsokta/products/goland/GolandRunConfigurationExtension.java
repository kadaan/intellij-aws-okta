package com.ensighten.awsokta.products.goland;

import com.ensighten.awsokta.platform.ui.RunConfigSettingsEditor;
import com.goide.execution.GoRunConfigurationBase;
import com.goide.execution.extension.GoRunConfigurationExtension;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.ensighten.awsokta.platform.AwsOktaEnvironmentVariables;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GolandRunConfigurationExtension extends GoRunConfigurationExtension {

    @Nullable
    @Override
    protected String getEditorTitle() {
        return RunConfigSettingsEditor.getEditorTitle();
    }

    @Override
    protected void patchCommandLine(
            @NotNull GoRunConfigurationBase<?> goRunConfigurationBase,
            @Nullable RunnerSettings runnerSettings,
            @NotNull GeneralCommandLine generalCommandLine,
            @NotNull String runnerId
    )
            throws ExecutionException
    {
        Map<String, String> newEnv = new AwsOktaEnvironmentVariables(
                RunConfigSettingsEditor.getAwsOktaSettings(goRunConfigurationBase)
        )
                .render(
                        goRunConfigurationBase.getProject(),
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
    protected void validateConfiguration(@NotNull GoRunConfigurationBase configuration, boolean isExecution) throws Exception {
        RunConfigSettingsEditor.validateConfiguration(configuration, isExecution);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return RunConfigSettingsEditor.getSerializationId();
    }

    @Override
    protected void readExternal(@NotNull GoRunConfigurationBase runConfiguration, @NotNull Element element) {
        RunConfigSettingsEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void writeExternal(@NotNull GoRunConfigurationBase runConfiguration, @NotNull Element element) {
        RunConfigSettingsEditor.writeExternal(runConfiguration, element);
    }

    @Nullable
    @Override
    public <P extends GoRunConfigurationBase<?>> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new RunConfigSettingsEditor<P>(configuration);
    }

    @Override
    public boolean isApplicableFor(@NotNull GoRunConfigurationBase goRunConfigurationBase) {
        return true;
    }

    @Override
    public boolean isEnabledFor(@NotNull GoRunConfigurationBase goRunConfigurationBase, @Nullable RunnerSettings runnerSettings) {
        return true;
    }
}
