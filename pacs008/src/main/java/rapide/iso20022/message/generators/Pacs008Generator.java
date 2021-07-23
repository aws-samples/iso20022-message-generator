/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators;

import com.prowidesoftware.swift.model.mx.AbstractMX;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import lombok.extern.slf4j.Slf4j;
import rapide.iso20022.cli.config.ConfigProperties;
import rapide.iso20022.data.bic.repository.BICRepository;
import rapide.iso20022.data.lei.repository.LegalEntityRepository;
import rapide.iso20022.message.generators.interfaces.IGenerator;
import rapide.iso20022.message.pacs008.Pacs008Message;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Pacs008Generator implements IGenerator {
    private LegalEntityRepository legalEntityRepository;
    private BICRepository bicRepository;
    private ConfigProperties configProperties;

    public void setLegalEntityRepository(LegalEntityRepository legalEntityRepository)
    {
        this.legalEntityRepository=legalEntityRepository;
    }

    public void setBICRepository(BICRepository bicRepository)
    {
        this.bicRepository=bicRepository;
    }

    public void setConfigProperties(ConfigProperties configProperties)
    {
        this.configProperties=configProperties;
    }

    @Override
    public List<AbstractMX> generate() {
        List<AbstractMX> messages = new ArrayList();
        try {
            Pacs008Message message = new Pacs008Message();
            MxPacs00800108 mx =  message.generateRandomMessage(legalEntityRepository, bicRepository, configProperties);
            messages.add(mx);

        } catch (Exception e) {
            log.error("Error running Data Loader ", e);
        }

        return messages;
    }
}
