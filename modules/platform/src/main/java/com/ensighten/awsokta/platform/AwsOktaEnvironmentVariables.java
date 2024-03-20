package com.ensighten.awsokta.platform;

import com.ensighten.awsokta.platform.commands.AwsOktaCmd;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.project.Project;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class AwsOktaEnvironmentVariables {
    private static final String NOTIFICATION_GROUP = "com.ensighten.awsokta";

    private final AwsOktaSettings awsOktaSettings;

    public Map<String, String> render(
            @NotNull Project project,
            @NotNull Map<String, String> runConfigEnv,
            boolean includeParentEnv
    ) throws ExecutionException {
        if (awsOktaSettings == null ||
                awsOktaSettings.getAwsOktaProfile() == null ||
                awsOktaSettings.getAwsOktaProfile().isEmpty() ||
                awsOktaSettings.getAwsOktaProfile().equals(AwsOktaSettings.NO_PROFILE)
        ) {
            return null;
        }

        Map<String, String> result = new HashMap<>();

        if (includeParentEnv) {
            result.putAll(
                    new GeneralCommandLine()
                            .withParentEnvironmentType(
                                    GeneralCommandLine.ParentEnvironmentType.CONSOLE
                            )
                            .getParentEnvironment()
            );
        }

        result.putAll(runConfigEnv);

        AwsOktaCmd cmd = new AwsOktaCmd();
        Map<String, String> awsOktaResult = cmd.importAwsOkta(awsOktaSettings.getAwsOktaProfile());

        awsOktaResult.forEach(result::putIfAbsent);

        return result;
    }
}
