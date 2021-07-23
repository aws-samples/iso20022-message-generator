/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.data.lei.repository;

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
import rapide.iso20022.data.lei.model.LegalEntity;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("unittest")
public class LegalEntityRepositoryTest{
    @Configuration
    @EnableJpaRepositories(basePackages = {"rapide.iso20022.data.lei.repository"})
    @EntityScan(basePackages = {"rapide.iso20022.data.lei.model"})
    static class LEIRepositoryTestContextConfiguration {
    }

    @Autowired
    private LegalEntityRepository legalEntityRepository;

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void resetDb() {
        legalEntityRepository.deleteAll();
    }

    @Test
    public void whenFindByID_thenReturnLegalEntity() {

        // when
        Optional<LegalEntity> entities = legalEntityRepository.findById("335800BY3CB8W1JK9L13");
        assertThat(entities.get().getId())
                .isEqualTo("335800BY3CB8W1JK9L13");
    }

    @Test
    public void whenFindAll_thenReturnLegalEntities() {

        // when
        List<LegalEntity> entities = legalEntityRepository.findAll();
        log.info("Total Legal Entities: " + entities.size());

        // then
        assertThat(entities.size())
                .isEqualTo(5000);
    }

}
