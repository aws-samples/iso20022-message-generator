/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.cli.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rapide.iso20022.data.bic.repository.BICRepository;
import rapide.iso20022.data.lei.repository.LegalEntityRepository;
import rapide.iso20022.message.generators.Pacs008Generator;
import rapide.iso20022.message.pacs008.Pacs008Validator;


@Configuration
public class Config {
    @Autowired
    private LegalEntityRepository legalEntityRepository;

    @Autowired
    private BICRepository bicRepository;

    @Autowired
    private ConfigProperties configProperties;

    @Bean
    public Pacs008Generator getPacs008Generator() {
        var entity = new Pacs008Generator();
        entity.setLegalEntityRepository(legalEntityRepository);
        entity.setBICRepository(bicRepository);
        entity.setConfigProperties(configProperties);

        return entity;
    }

    @Bean
    public Pacs008Validator getPacs008Validator() {
        var entity = new Pacs008Validator();
        entity.setConfigProperties(configProperties);
        return entity;
    }

}
