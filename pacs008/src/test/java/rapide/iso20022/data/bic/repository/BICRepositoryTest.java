/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.data.bic.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rapide.iso20022.data.bic.model.BICRecord;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("unittest")
public class BICRepositoryTest {
    @Configuration
    @EnableJpaRepositories(basePackages = {"rapide.iso20022.data.bic.repository"})
    @EntityScan(basePackages = {"rapide.iso20022.data.bic.model"})
    static class BICRepositoryTestContextConfiguration {
    }

    @Autowired
    private BICRepository bicRepository;

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void resetDb() {
        bicRepository.deleteAll();
    }

    @Test
    public void whenFindByID_thenReturnBIC() {

        // when
        Optional<BICRecord> bicRecord = bicRepository.findById("ABABHKHJ-XXX");
        assertThat(bicRecord.get().getInstitutionName())
                .isEqualTo("DEMO BANK LTD");
    }

    @Test
    public void whenFindAll_thenReturnAllBICRecords() {

        // when
        List<BICRecord> entities = bicRepository.findAll();
        log.info("Total BIC Records: " + entities.size());

        // then
        assertThat(entities.size())
                .isEqualTo(5);
    }
}
