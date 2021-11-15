/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.data.bic.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rapide.iso20022.data.bic.model.BICRecord;

import java.util.List;
import java.util.Set;

@Repository
public interface BICRepository extends JpaRepository<BICRecord, String> {
    Set<BICRecord> findByBIC(String BICCode);
    Set<BICRecord> findByBranch(String BranchCode);
    List<BICRecord> findByCountryCode(String countryCode);
    List<BICRecord> findAllExcludeInvalid();
}