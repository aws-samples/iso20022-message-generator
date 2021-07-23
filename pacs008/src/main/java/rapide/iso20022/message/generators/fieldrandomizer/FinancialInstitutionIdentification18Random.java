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
public class FinancialInstitutionIdentification18Random extends FinancialInstitutionIdentification18 {

    protected BICRepository bicRepository;
    protected List<String> restrictedBICList;


    public FinancialInstitutionIdentification18Random(BICRepository bicRepository, List<String> restrictedBICList){
        this.bicRepository = bicRepository;
        this.restrictedBICList = restrictedBICList;

        if (restrictedBICList==null) {
            // get random BIC from repo
            BICRecord query;

            List<BICRecord> bicPage = bicRepository.findAllExcludeInvalid();
            int idx = (int) (Math.random() *  bicPage.size());

            if (!bicPage.isEmpty()) {
                query = bicPage.get(idx);
                this.setBICFI(query.getBicCode());
//            this.setNm(Common.StringStripper(query.getInstitutionName(),140));
//            this.setPstlAdr(getBICAddress(query));
            } else
                log.error("Error Generating Random BIC number");
        }
        else
        {
            boolean validBic = false;
            while(!validBic)
            {
                int idx = (int) (Math.random() * restrictedBICList.size());
                String bic = restrictedBICList.get(idx);
                if (!bic.endsWith("0") && !bic.endsWith("1"))
                {
                    this.setBICFI(restrictedBICList.get(idx));
                    validBic=true;
                }
                else
                    log.warn("Warning, provided Bic is not valid. trying another one.");
            }
        }
    }

    public PostalAddress24 getBICAddress(BICRecord record) {
        PostalAddress24 address = new PostalAddress24();
        address.setCtry(record.getCountryCode());
        address.setStrtNm(StringUtils.removeNonPrintableChars(StringUtils.StringStripper(record.getPhysicalAddress1(),70)));
        address.setTwnNm(StringUtils.removeNonPrintableChars(StringUtils.StringStripper(record.getCityHeading(),35)));
        return address;
    }
}
