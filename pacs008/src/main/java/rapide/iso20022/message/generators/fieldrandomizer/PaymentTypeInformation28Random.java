/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import com.prowidesoftware.swift.model.mx.dic.CategoryPurpose1Choice;
import com.prowidesoftware.swift.model.mx.dic.PaymentTypeInformation28;
import com.prowidesoftware.swift.model.mx.dic.Priority2Code;
import lombok.extern.slf4j.Slf4j;
import rapide.iso20022.cli.config.ConfigProperties;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
public class PaymentTypeInformation28Random {

    public static PaymentTypeInformation28 getPaymentTypeInformation28(ConfigProperties configProperties)
    {
        PaymentTypeInformation28 paymentTypeInformation = new PaymentTypeInformation28();

        CategoryPurpose1Choice categoryPurpose = new CategoryPurpose1Choice();
        List<String> purposeCodes = configProperties.getPurposeCode();
        SecureRandom random = new SecureRandom();
        int index = random.nextInt(purposeCodes.size());
        categoryPurpose.setCd(purposeCodes.get(index));
        paymentTypeInformation.setCtgyPurp(categoryPurpose);

        index = random.nextInt( Priority2Code.values().length);
        paymentTypeInformation.setInstrPrty(Priority2Code.values()[index]);

        return paymentTypeInformation;
    }
}
