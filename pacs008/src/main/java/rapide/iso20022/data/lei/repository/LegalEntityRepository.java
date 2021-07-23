/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.data.lei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rapide.iso20022.data.lei.model.LegalEntity;

import java.util.Set;

@Repository
public interface LegalEntityRepository extends JpaRepository<LegalEntity, String> {
    Set<LegalEntity> findByLegalCountry(String country);
    Set<LegalEntity> findByHQCountry(String country);
}
