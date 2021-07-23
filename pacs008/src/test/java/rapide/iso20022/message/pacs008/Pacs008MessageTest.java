/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.pacs008;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import rapide.iso20022.message.generators.Pacs008Generator;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("unittest")
public class Pacs008MessageTest {

    @Autowired
    private Pacs008Generator pacs008Generator;

//    @Test
//    public void TestMessageGeneration() {
//        try {
//            pacs008Generator.generate();
//        } catch (Exception e) {
//            log.error("TestMessageGeneration Failed",e);
//            assert(false);
//        }
//        assert(true);
//    }
}
