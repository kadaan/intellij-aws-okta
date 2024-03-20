package com.ensighten.awsokta.products.idea;

import com.ensighten.awsokta.platform.ui.RunConfigSettingsEditor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.ensighten.awsokta.platform.AwsOktaEnvironmentVariables;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class IdeaRunConfigurationExtension extends RunConfigurationExtension {

    @Nullable
    @Override
    protected String getEditorTitle() {
        return RunConfigSettingsEditor.getEditorTitle();
    }

    @Nullable
    @Override
    protected <P extends RunConfigurationBase<?>> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new RunConfigSettingsEditor<P>(configuration);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return RunConfigSettingsEditor.getSerializationId();
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws WriteExternalException {
        RunConfigSettingsEditor.writeExternal(runConfiguration, element);
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {
        RunConfigSettingsEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void validateConfiguration(@NotNull RunConfigurationBase configuration, boolean isExecution) throws Exception {
        RunConfigSettingsEditor.validateConfiguration(configuration, isExecution);
    }

    /**
     * Unlike other extensions the IDEA extension
     * calls this method instead of RunConfigurationExtensionBase#patchCommandLine method
     * that we could have used to update environment variables.
     */
    @Override
    public <T extends RunConfigurationBase<?>> void updateJavaParameters(
            @NotNull final T configuration,
            @NotNull final JavaParameters params,
            final RunnerSettings runnerSettings
    ) throws ExecutionException {
        Map<String, String> newEnv = new AwsOktaEnvironmentVariables(
                RunConfigSettingsEditor.getAwsOktaSettings(configuration)
        )
                .render(
                        configuration.getProject(),
                        params.getEnv(),
                        params.isPassParentEnvs()
                );

        if (newEnv == null) {
            return;
        }

        // there is a chance that env is an immutable map,
        // that is why it is safer to replace it instead of updating it
        params.setEnv(new HashMap<>(newEnv));
    }

    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase configuration) {
        return true;
    }

    @Override
    public boolean isEnabledFor(@NotNull RunConfigurationBase applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }

}
