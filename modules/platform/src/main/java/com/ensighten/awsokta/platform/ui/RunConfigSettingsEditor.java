package com.ensighten.awsokta.platform.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.Key;
import com.ensighten.awsokta.platform.commands.AwsOktaCmd;
import com.ensighten.awsokta.platform.AwsOktaSettings;
import lombok.val;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class RunConfigSettingsEditor<T extends RunConfigurationBase<?>> extends SettingsEditor<T> {
    public static final Key<AwsOktaSettings> USER_DATA_KEY = new Key<>("AwsOkta Settings");

    private static final String SERIALIZATION_ID = "com.ensighten.awsokta";

    private static final String FIELD_AWS_OKTA_PROFILE = "AWS_OKTA_PROFILE";

    private final RunConfigSettingsPanel editor;

    public RunConfigSettingsEditor(RunConfigurationBase<?> configuration) {
        editor = new RunConfigSettingsPanel(configuration);
    }

    public static void readExternal(@NotNull RunConfigurationBase<?> configuration, @NotNull Element element) {
        String awsOktaProfile = readString(element, FIELD_AWS_OKTA_PROFILE);

        AwsOktaSettings state = new AwsOktaSettings(awsOktaProfile);
        configuration.putCopyableUserData(USER_DATA_KEY, state);
    }

    public static void writeExternal(@NotNull RunConfigurationBase<?> configuration, @NotNull Element element) {
        AwsOktaSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null) {
            writeString(element, FIELD_AWS_OKTA_PROFILE, state.getAwsOktaProfile());
        }
    }

    private static String readString(Element element, String field) {
        return JDOMExternalizerUtil.readField(element, field);
    }

    private static void writeString(Element element, String field, String value) {
        JDOMExternalizerUtil.writeField(element, field, value);
    }

    public static AwsOktaSettings getAwsOktaSettings(@NotNull RunConfigurationBase<?> runConfigurationBase) {
        return runConfigurationBase.getCopyableUserData(USER_DATA_KEY);
    }

    public static void validateConfiguration(@NotNull RunConfigurationBase<?> configuration, boolean isExecution) throws ExecutionException {
    }

    @NotNull
    @Contract(pure = true)
    public static String getSerializationId() {
        return SERIALIZATION_ID;
    }

    public static String getEditorTitle() {
        return "AwsOkta";
    }

    @Override
    protected void resetEditorFrom(@NotNull T configuration) {
        AwsOktaSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null) {
            editor.setState(state);
        }
    }

    @Override
    protected void applyEditorTo(@NotNull T configuration) throws ConfigurationException {
        configuration.putCopyableUserData(USER_DATA_KEY, editor.getState());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return editor;
    }
}
