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
public class BranchAndFinancialInstitutionIdentification6Random extends BranchAndFinancialInstitutionIdentification6 {
    protected BICRepository bicRepository;
    protected List<String> restrictedBICList;

    public BranchAndFinancialInstitutionIdentification6Random(BICRepository bicRepository) {
        this.bicRepository = bicRepository;
        this.setFinInstnId(new FinancialInstitutionIdentification18Random(this.bicRepository, null));
    }

    public BranchAndFinancialInstitutionIdentification6Random(BICRepository bicRepository, List<String> restrictedBICList) {
        this.bicRepository = bicRepository;
        this.restrictedBICList = restrictedBICList;

        this.setFinInstnId(new FinancialInstitutionIdentification18Random(this.bicRepository, restrictedBICList));
    }

}
