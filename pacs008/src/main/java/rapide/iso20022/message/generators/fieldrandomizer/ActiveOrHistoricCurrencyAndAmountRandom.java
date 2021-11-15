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
public class ActiveOrHistoricCurrencyAndAmountRandom {

    public static ActiveOrHistoricCurrencyAndAmount getActiveOrHistoricCurrencyAndAmount(BigDecimal settleAmount, String currency)
    {
        ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = new ActiveOrHistoricCurrencyAndAmount();
        SecureRandom random = new SecureRandom();
        BigDecimal fxRate = BigDecimal.valueOf(10 + random.nextInt(90));
        activeOrHistoricCurrencyAndAmount.setCcy(currency);
        //applying FX
        activeOrHistoricCurrencyAndAmount.setValue((settleAmount.subtract(fxRate)).setScale(2, RoundingMode.CEILING));
        return activeOrHistoricCurrencyAndAmount;
    }

    // Assigns random currency
    public static ActiveOrHistoricCurrencyAndAmount getActiveOrHistoricCurrencyAndAmount(ConfigProperties configProperties)
    {
        ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = new ActiveOrHistoricCurrencyAndAmount();
        activeOrHistoricCurrencyAndAmount.setCcy(Helper.getRandomCurrencyCode(configProperties));
        activeOrHistoricCurrencyAndAmount.setValue(Helper.getLongDigitsRandomNumber(5000,10000000000L));
        return activeOrHistoricCurrencyAndAmount;
    }

    // Assigns currency for the country code provided
    public static ActiveOrHistoricCurrencyAndAmount getActiveOrHistoricCurrencyAndAmount(ConfigProperties configProperties,
        String countryCode)
    {
        ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = new ActiveOrHistoricCurrencyAndAmount();
        activeOrHistoricCurrencyAndAmount.setCcy(Helper.getCountryCurrencyCode(configProperties, countryCode));
        activeOrHistoricCurrencyAndAmount.setValue(Helper.getLongDigitsRandomNumber(5000,10000000000L));
        return activeOrHistoricCurrencyAndAmount;
    }
}
