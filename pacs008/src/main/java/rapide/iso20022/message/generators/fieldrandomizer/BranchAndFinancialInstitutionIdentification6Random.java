/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import com.prowidesoftware.swift.model.mx.dic.BranchAndFinancialInstitutionIdentification6;
import lombok.extern.slf4j.Slf4j;
import rapide.iso20022.data.bic.repository.BICRepository;

import java.util.List;

@Slf4j
public class BranchAndFinancialInstitutionIdentification6Random {

    public static BranchAndFinancialInstitutionIdentification6
        getBranchAndFinancialInstitutionIdentification(BICRepository bicRepository, List<String> sourceBICList,
                                                                    String countryCode) {
        BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification =
                new BranchAndFinancialInstitutionIdentification6();
        branchAndFinancialInstitutionIdentification.setFinInstnId(
                FinancialInstitutionIdentification18Random.getFinancialInstitutionIdentification(bicRepository, sourceBICList, countryCode));

        return branchAndFinancialInstitutionIdentification;
    }

    public static BranchAndFinancialInstitutionIdentification6
        getBranchAndFinancialInstitutionIdentification(BICRepository bicRepository, List<String> sourceBICList) {
        BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification =
                new BranchAndFinancialInstitutionIdentification6();
        branchAndFinancialInstitutionIdentification.setFinInstnId(
                FinancialInstitutionIdentification18Random.getFinancialInstitutionIdentification(bicRepository, sourceBICList));

        return branchAndFinancialInstitutionIdentification;
    }

    public static BranchAndFinancialInstitutionIdentification6
        getBranchAndFinancialInstitutionIdentification(BICRepository bicRepository,
                                                   String countryCode) {
        BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification =
                new BranchAndFinancialInstitutionIdentification6();
        branchAndFinancialInstitutionIdentification.setFinInstnId(
                FinancialInstitutionIdentification18Random.getFinancialInstitutionIdentification(bicRepository, countryCode));

        return branchAndFinancialInstitutionIdentification;
    }

    public static BranchAndFinancialInstitutionIdentification6
        getBranchAndFinancialInstitutionIdentification(List<String> sourceBICList,
                                                   String countryCode) {
        BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification =
                new BranchAndFinancialInstitutionIdentification6();
        branchAndFinancialInstitutionIdentification.setFinInstnId(
                FinancialInstitutionIdentification18Random.getFinancialInstitutionIdentification(sourceBICList, countryCode));

        return branchAndFinancialInstitutionIdentification;
    }

    public static BranchAndFinancialInstitutionIdentification6
        getBranchAndFinancialInstitutionIdentification(BICRepository bicRepository) {
        BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification =
                new BranchAndFinancialInstitutionIdentification6();
        branchAndFinancialInstitutionIdentification.setFinInstnId(
                FinancialInstitutionIdentification18Random.getFinancialInstitutionIdentification(bicRepository));

        return branchAndFinancialInstitutionIdentification;
    }

    public static BranchAndFinancialInstitutionIdentification6
        getBranchAndFinancialInstitutionIdentification(List<String> sourceBICList) {
        BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification =
                new BranchAndFinancialInstitutionIdentification6();
        branchAndFinancialInstitutionIdentification.setFinInstnId(
                FinancialInstitutionIdentification18Random.getFinancialInstitutionIdentificationFromSourceList(sourceBICList));

        return branchAndFinancialInstitutionIdentification;
    }

}
