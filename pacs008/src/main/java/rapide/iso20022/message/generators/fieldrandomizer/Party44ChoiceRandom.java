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
public class Party44ChoiceRandom {
    public static Party44Choice getParty44ChoiceRandom(String bicFi)
    {
        Party44Choice party44Choice = new Party44Choice();
        BranchAndFinancialInstitutionIdentification6 fiid = new BranchAndFinancialInstitutionIdentification6();
        FinancialInstitutionIdentification18 instituteID = new FinancialInstitutionIdentification18();
        instituteID.setBICFI(bicFi);
        fiid.setFinInstnId(instituteID);
        party44Choice.setFIId(fiid);

        return party44Choice;
    }
}
