/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.cli.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "rapide")
public class ConfigProperties {
    private List<String> purposeCode;
    private List<HashMap<String, String>> currency;
    private List<HashMap<String, String>> validationSchema;
    private int invalidMessageRetry;

    private List<String> sourceBicList;
    private List<String> destinationBicList;
}
