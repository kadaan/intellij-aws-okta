package com.ensighten.awsokta.platform.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AwsOktaOutput {
    private String output;
    private boolean error;
}
