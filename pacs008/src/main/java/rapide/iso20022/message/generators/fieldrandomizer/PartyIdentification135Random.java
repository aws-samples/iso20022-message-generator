/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import com.prowidesoftware.swift.model.mx.dic.PartyIdentification135;
import com.prowidesoftware.swift.model.mx.dic.PostalAddress24;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import rapide.iso20022.data.lei.model.LegalEntity;
import rapide.iso20022.data.lei.repository.LegalEntityRepository;
import rapide.iso20022.util.StringUtils;

@Slf4j
public class PartyIdentification135Random extends PartyIdentification135 {
    LegalEntityRepository legalEntityRepository;

    //TODO: Override set with restriction for field size
    @Override
    public PartyIdentification135 setNm(String value) {
        return super.setNm(value);
    }

    public PartyIdentification135Random(LegalEntityRepository legalEntityRepository){
        this.legalEntityRepository = legalEntityRepository;

        // get random Legal Entity from repo
        Long recordCount = legalEntityRepository.count();
        int idx = (int)(Math.random() * recordCount);
        // log.info("Random Entity - " + idx);
        Page<LegalEntity> entityPage = legalEntityRepository.findAll(PageRequest.of(idx, 1));

        LegalEntity query;
        if (entityPage.hasContent()) {
            query = entityPage.getContent().get(0);

            this.setNm(StringUtils.removeNonPrintableChars(StringUtils.StringStripper(query.getLegalName(),140)));
            PostalAddress24 postalAddress = new PostalAddress24();
            postalAddress.setCtry(StringUtils.removeNonPrintableChars(query.getLegalAddressCountry()));
            postalAddress.setTwnNm(StringUtils.removeNonPrintableChars(StringUtils.removeNonPrintableChars(StringUtils.StringStripper(query.getLegalAddressCity(),35))));
            postalAddress.setPstCd(query.getLegalAddressPostalCode());
            postalAddress.setStrtNm(StringUtils.removeNonPrintableChars(StringUtils.removeNonPrintableChars(StringUtils.StringStripper(query.getLegalAddressFirstAddressLine(),70))));

            this.setPstlAdr(postalAddress);
        }
        else
            log.error("Error Generating Random BIC number");
    }
}
