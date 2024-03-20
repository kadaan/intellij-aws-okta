package com.ensighten.awsokta.products.rubymine;

import com.ensighten.awsokta.platform.ui.RunConfigSettingsEditor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.ensighten.awsokta.platform.AwsOktaEnvironmentVariables;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.run.configuration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.configuration.RubyRunConfigurationExtension;

import java.util.Map;

public class RubyMineRunConfigurationExtension extends RubyRunConfigurationExtension {

    @Nullable
    @Override
    protected String getEditorTitle() {
        return RunConfigSettingsEditor.getEditorTitle();
    }

    @Nullable
    @Override
    protected <P extends AbstractRubyRunConfiguration<?>> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new RunConfigSettingsEditor<P>(configuration);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return RunConfigSettingsEditor.getSerializationId();
    }

    @Override
    protected void writeExternal(@NotNull AbstractRubyRunConfiguration runConfiguration, @NotNull Element element) throws WriteExternalException {
        RunConfigSettingsEditor.writeExternal(runConfiguration, element);
    }

    @Override
    protected void readExternal(@NotNull AbstractRubyRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
        RunConfigSettingsEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void validateConfiguration(@NotNull AbstractRubyRunConfiguration configuration, boolean isExecution) throws Exception {
        RunConfigSettingsEditor.validateConfiguration(configuration, isExecution);
    }

    @Override
    protected void patchCommandLine(
            @NotNull AbstractRubyRunConfiguration<?> configuration,
            @Nullable RunnerSettings runnerSettings,
            @NotNull GeneralCommandLine cmdLine,
            @NotNull String runnerId
    ) throws ExecutionException {
        Map<String, String> newEnv = new AwsOktaEnvironmentVariables(
                RunConfigSettingsEditor.getAwsOktaSettings(configuration)
        )
                .render(
                        configuration.getProject(),
                        cmdLine.getEnvironment(),
                        cmdLine.isPassParentEnvironment()
                );

        if (newEnv == null) {
            return;
        }

        cmdLine.getEnvironment().clear();
        cmdLine.getEnvironment().putAll(newEnv);
    }

    //

    @Override
    public boolean isApplicableFor(@NotNull AbstractRubyRunConfiguration configuration) {
        return true;
    }

    @Override
    public boolean isEnabledFor(@NotNull AbstractRubyRunConfiguration applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }
}
