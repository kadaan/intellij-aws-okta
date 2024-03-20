package com.ensighten.awsokta.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AwsOktaSettings {
    public static final String NO_PROFILE = "<NONE>";

    private final String awsOktaProfile;
}
