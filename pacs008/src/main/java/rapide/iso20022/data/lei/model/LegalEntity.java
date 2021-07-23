/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.data.lei.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "LEI")
@NamedQuery(name = "LegalEntity.findByLegalCountry",
        query = "select l from LegalEntity l where l.legalAddressCountry = ?1")
@NamedQuery(name = "LegalEntity.findByHQCountry",
        query = "select l from LegalEntity l where l.headquartersAddressCountry = ?1")
public class LegalEntity implements Serializable {
    @Id
    @Column(name="ID")
    private String id;

    @Column(name="legal_name")
    private String legalName;

    @Column(name="legal_name_lang")
    private String legalNameLang;

    // Legal address fields
    @Column(name="legal_address_lang")
    private String legalAddressLang;

    @Column(name="legal_address_first_address_line")
    private String legalAddressFirstAddressLine;

    @Column(name="legal_address_additional_address_line1")
    private String legalAddressAdditionalAddressLine1;

    @Column(name="legal_address_additional_address_line2")
    private String legalAddressAdditionalAddressLine2;

    @Column(name="legal_address_city")
    private String legalAddressCity;

    @Column(name="legal_address_region")
    private String legalAddressRegion;

    @Column(name="legal_address_country")
    private String legalAddressCountry;

    @Column(name="legal_address_postal_code")
    private String legalAddressPostalCode;

    //HeadquartersAddress

    @Column(name="headquarters_address_lang")
    private String headquartersAddressLang;

    @Column(name="headquarters_address_first_address_line")
    private String headquartersAddressFirstAddressLine;

    @Column(name="headquarters_address_additional_address_line1")
    private String headquartersAddressAdditionalAddressLine1;

    @Column(name="headquartersAddressAdditionalAddressLine2")
    private String headquarters_address_additional_address_line2;

    @Column(name="headquarters_address_city")
    private String headquartersAddressCity;

    @Column(name="headquarters_address_region")
    private String headquartersAddressRegion;

    @Column(name="headquarters_address_country")
    private String headquartersAddressCountry;

    @Column(name="headquarters_address_postal_code")
    private String headquartersAddressPostalCode;

}
