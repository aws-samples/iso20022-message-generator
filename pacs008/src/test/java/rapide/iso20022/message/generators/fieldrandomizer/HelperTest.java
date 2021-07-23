/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("unittest")
public class HelperTest {

    @Test
    public void TestUUIDGeneration() {
        String uuid = null;
        uuid = Helper.getUUIDv4();
        assertThat(uuid.length()).isEqualTo(36);
    }

    @Test
    public void TestCharsRandomGeneration() {
        String chars = Helper.getRandomAlphaNumeric(16);
        assertThat(chars.length()).isEqualTo(16);
    }

}
