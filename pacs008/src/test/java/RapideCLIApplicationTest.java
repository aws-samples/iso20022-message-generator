/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import rapide.iso20022.cli.RapideCLIApplication;

@ContextConfiguration(classes = {RapideCLIApplication.class})
@SpringBootTest
public class RapideCLIApplicationTest {
    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}
