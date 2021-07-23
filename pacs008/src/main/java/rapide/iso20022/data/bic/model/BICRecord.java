/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.data.bic.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "BIC")
@NamedQuery(name = "BICRecord.findByBIC",
        query = "select b from BICRecord b where b.bicCode = ?1")
@NamedQuery(name = "BICRecord.findByBranch",
        query = "select b from BICRecord b where b.branchCode = ?1")
@NamedQuery(name = "BICRecord.findAllExcludeInvalid",
        query="select b from BICRecord b where NOT (b.bicCode LIKE '%0' or b.bicCode LIKE '%1')")
public class BICRecord implements Serializable {
    @Id
    @Column(name="ID")
    private String id;

    @Column(name="BIC_code")
    private String bicCode;

    @Column(name="branch_code")
    private String branchCode;

    @Column(name="institution_name")
    private String institutionName;

    @Column(name="city_heading")
    private String cityHeading;

    @Column(name="physical_address1")
    private String physicalAddress1;

    @Column(name="physical_address2")
    private String physicalAddress2;

    @Column(name="physical_address3")
    private String physicalAddress3;

    @Column(name="location")
    private String location;

    @Column(name="country_name")
    private String country;

    @Column(name="country_code")
    private String countryCode;
}
