package com.ensighten.awsokta.platform.commands;

import com.google.common.base.Splitter;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.ini4j.Ini;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AwsOktaCmd {
    private static final String GROUP_DISPLAY_ID = "AwsOkta";
    private static final Logger LOG = Logger.getInstance(AwsOktaCmd.class);

    public Collection<String> getProfiles() {
        java.util.List<String> profiles = new ArrayList<>();
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".aws", "config");
            if (Files.exists(path)) {
                Ini ini = new Ini(path.toFile());
                for (String key : ini.keySet()) {
                    if (key.startsWith("profile ")) {
                        profiles.add(key.substring(8));
                    }
                }
            }
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "AwsOkta failed to load profiles", NotificationType.WARNING));
        }
        return profiles;
    }

    public Map<String, String> importAwsOkta(String profile) {
        Map<String, String> returnMap = new HashMap<>();
        try {
            AwsOktaOutput output = run("print", profile, "--output-type=delim");
            if (output.isError()) {
                Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "AwsOkta failed while calling aws-okta", NotificationType.WARNING));
                return returnMap;
            }

            // Output will be empty if there is no aws-okta support
            if (Objects.equals(output.getOutput(), "")) {
                return returnMap;
            }

            Iterable<String> variables = Splitter.on(";")
                    .omitEmptyStrings()
                    .trimResults()
                    .split(output.getOutput());
            for (String variable : variables) {
                List<String> parts = Splitter.on("=")
                        .limit(2)
                        .omitEmptyStrings()
                        .trimResults()
                        .splitToList(variable);
                if (parts.size() == 2) {
                    returnMap.put(parts.get(0), parts.get(1));
                }
            }

            return returnMap;
        } catch (Exception e) {
            LOG.error(e);
            return returnMap;
        }
    }

    private AwsOktaOutput run(String... args) throws ExecutionException, InterruptedException, IOException {
        String[] newArgArray = new String[1];
        newArgArray[0] = "aws-okta";
        newArgArray = ArrayUtils.addAll(newArgArray, args);

        GeneralCommandLine cli = new GeneralCommandLine(newArgArray);
        Process process = cli.createProcess();

        if (process.waitFor() != 0) {
            String stdErr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            return new AwsOktaOutput(stdErr, true);
        }

        String stdOut = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);

        return new AwsOktaOutput(stdOut, false);
    }
}
