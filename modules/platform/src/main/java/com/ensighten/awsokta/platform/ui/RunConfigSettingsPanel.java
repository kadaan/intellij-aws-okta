package com.ensighten.awsokta.platform.ui;

import com.ensighten.awsokta.platform.commands.AwsOktaCmd;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.ensighten.awsokta.platform.AwsOktaSettings;
import com.intellij.ui.MutableCollectionComboBoxModel;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.util.Collection;
import java.util.List;

public class RunConfigSettingsPanel extends JPanel {
    private final ComboBox<String> awkOktaProfile;

    public RunConfigSettingsPanel(RunConfigurationBase<?> configuration) {
        awkOktaProfile = new ComboBox<>(createModel(new AwsOktaCmd().getProfiles(), AwsOktaSettings.NO_PROFILE));
        setLayout(new BorderLayout());
        add(new ComponentPanelBuilder(awkOktaProfile).withLabel("AWS-Okta Profile:").createPanel(), BorderLayout.WEST);
    }

    public AwsOktaSettings getState() {
        return new AwsOktaSettings(
                awkOktaProfile.getItem()
        );
    }

    public void setState(AwsOktaSettings state) {
        awkOktaProfile.setItem(state.getAwsOktaProfile());
    }

    @NotNull
    private static MutableCollectionComboBoxModel<String> createModel(@NotNull Collection<String> values, @NotNull String defaultValue) {
        List<String> items = ContainerUtil.newArrayList(defaultValue);
        items.addAll(ContainerUtil.sorted(values));
        return new MutableCollectionComboBoxModel<>(items, defaultValue);
    }
}
