/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import com.prowidesoftware.swift.model.mx.dic.FinancialInstitutionIdentification18;
import com.prowidesoftware.swift.model.mx.dic.PostalAddress24;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import rapide.iso20022.data.bic.model.BICRecord;
import rapide.iso20022.data.bic.repository.BICRepository;
import lombok.extern.slf4j.Slf4j;
import rapide.iso20022.util.StringUtils;

import java.util.List;

@Slf4j
public class FinancialInstitutionIdentification18Random {

    public static FinancialInstitutionIdentification18
        getFinancialInstitutionIdentification(BICRepository bicRepository, List<String> sourceBICList, String countryCode) {
        if (sourceBICList != null && !sourceBICList.isEmpty())
            return getFinancialInstitutionIdentification(sourceBICList, countryCode);
        else
            return getFinancialInstitutionIdentification(bicRepository, countryCode);
    }

    public static FinancialInstitutionIdentification18
        getFinancialInstitutionIdentification(BICRepository bicRepository, List<String> sourceBICList) {
        if (sourceBICList != null && !sourceBICList.isEmpty())
            return getFinancialInstitutionIdentificationFromSourceList(sourceBICList);
        else
            return getFinancialInstitutionIdentification(bicRepository);
    }

    public static FinancialInstitutionIdentification18
        getFinancialInstitutionIdentification(BICRepository bicRepository,
                                             String countryCode) {
        // get random BIC from repo by country code
        if (countryCode != null && countryCode.length() == 2) {
            BICRecord query;

            FinancialInstitutionIdentification18 financialInstitutionIdentification =
                    new FinancialInstitutionIdentification18();

            List<BICRecord> bicPage = bicRepository.findByCountryCode(countryCode);
            int idx = (int) (Math.random() * bicPage.size());

            if (!bicPage.isEmpty()) {
                query = bicPage.get(idx);
                financialInstitutionIdentification.setBICFI(query.getBicCode());
            } else {
                log.error("Error Generating Random BIC number");
            }

            return financialInstitutionIdentification;
        } else {
            return getFinancialInstitutionIdentification(bicRepository);
        }
    }

    public static FinancialInstitutionIdentification18 getFinancialInstitutionIdentification(BICRepository bicRepository) {
        // get random BIC from repo
        BICRecord query;

        FinancialInstitutionIdentification18 financialInstitutionIdentification =
                new FinancialInstitutionIdentification18();

        List<BICRecord> bicPage = bicRepository.findAllExcludeInvalid();
        int idx = (int) (Math.random() * bicPage.size());

        if (!bicPage.isEmpty()) {
            query = bicPage.get(idx);
            financialInstitutionIdentification.setBICFI(query.getBicCode());
//            this.setNm(Common.StringStripper(query.getInstitutionName(),140));
//            this.setPstlAdr(getBICAddress(query));
        } else {
            log.error("Error Generating Random BIC number");
        }

        return financialInstitutionIdentification;
    }

    public static FinancialInstitutionIdentification18
        getFinancialInstitutionIdentification(List<String> sourceBICList, String countryCode) {
        FinancialInstitutionIdentification18 financialInstitutionIdentification =
                new FinancialInstitutionIdentification18();

        boolean validBic = false;
        while(!validBic) {
            int idx = (int) (Math.random() * sourceBICList.size());
            String bic = sourceBICList.get(idx);
            if (!bic.endsWith("0") && !bic.endsWith("1") && Helper.getCountryFromBIC(bic).equals(countryCode))
            {
                financialInstitutionIdentification.setBICFI(sourceBICList.get(idx));
                validBic = true;
            }
            else
                log.warn("Warning, provided Bic is not valid. trying another one.");
        }

        return financialInstitutionIdentification;
    }

    public static FinancialInstitutionIdentification18
    getFinancialInstitutionIdentificationFromSourceList(List<String> sourceBICList) {
        FinancialInstitutionIdentification18 financialInstitutionIdentification =
                new FinancialInstitutionIdentification18();

        boolean validBic = false;
        while(!validBic) {
            int idx = (int) (Math.random() * sourceBICList.size());
            String bic = sourceBICList.get(idx);
            if (!bic.endsWith("0") && !bic.endsWith("1"))
            {
                financialInstitutionIdentification.setBICFI(sourceBICList.get(idx));
                validBic = true;
            }
            else
                log.warn("Warning, provided Bic is not valid. trying another one.");
        }

        return financialInstitutionIdentification;
    }

    public static PostalAddress24 getBICAddress(BICRecord record) {
        PostalAddress24 address = new PostalAddress24();
        address.setCtry(record.getCountryCode());
        address.setStrtNm(StringUtils.removeNonPrintableChars(StringUtils.StringStripper(record.getPhysicalAddress1(),70)));
        address.setTwnNm(StringUtils.removeNonPrintableChars(StringUtils.StringStripper(record.getCityHeading(),35)));
        return address;
    }
}
