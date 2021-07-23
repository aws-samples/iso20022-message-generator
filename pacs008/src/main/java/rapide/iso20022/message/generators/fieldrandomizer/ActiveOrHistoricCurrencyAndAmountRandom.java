/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import lombok.extern.slf4j.Slf4j;
import rapide.iso20022.cli.config.ConfigProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class ActiveOrHistoricCurrencyAndAmountRandom extends ActiveOrHistoricCurrencyAndAmount {

    ConfigProperties configProperties;

    public ActiveOrHistoricCurrencyAndAmountRandom(BigDecimal settleAmount, String currency)
    {
        SecureRandom random = new SecureRandom();
        BigDecimal fxRate = BigDecimal.valueOf(10 + random.nextInt(90));
        this.setCcy(currency);
        //applying FX
        this.setValue((settleAmount.subtract(fxRate)).setScale(2, RoundingMode.CEILING));
    }

    public ActiveOrHistoricCurrencyAndAmountRandom(ConfigProperties configProperties)
    {
        this.configProperties = configProperties;
        List<HashMap<String, String>> currency = configProperties.getCurrency();
        SecureRandom random = new SecureRandom();
        int index = random.nextInt(currency.size());
        this.setCcy(currency.get(index).get("Code"));
        this.setValue(Helper.getLongDigitsRandomNumber(5000,10000000000L));
    }
}
