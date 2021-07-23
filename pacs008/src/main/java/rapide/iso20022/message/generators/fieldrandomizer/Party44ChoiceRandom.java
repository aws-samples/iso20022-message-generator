/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import com.prowidesoftware.swift.model.mx.dic.BranchAndFinancialInstitutionIdentification6;
import com.prowidesoftware.swift.model.mx.dic.FinancialInstitutionIdentification18;
import com.prowidesoftware.swift.model.mx.dic.Party44Choice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Party44ChoiceRandom extends Party44Choice {
    public Party44ChoiceRandom(String BICFI)
    {
        BranchAndFinancialInstitutionIdentification6 fiid = new BranchAndFinancialInstitutionIdentification6();
        FinancialInstitutionIdentification18 instituteID = new FinancialInstitutionIdentification18();
        instituteID.setBICFI(BICFI);
        fiid.setFinInstnId(instituteID);
        this.setFIId(fiid);
    }
}
