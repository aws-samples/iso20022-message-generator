/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.cli;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import rapide.iso20022.util.FileUtils;

import static java.nio.file.Files.deleteIfExists;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Slf4j
@ActiveProfiles("unittest")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageGeneratorCommandTest {
    private final PrintStream standardOut = System.out;
    private final static ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Autowired
    private IFactory cliFactory;

    @Autowired
    private MessageGeneratorCommand command;

    @BeforeAll()
    public static void setUp() {
        log.info("Setup method executed.");
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @Test
    @Order(2)
    public void testCommandDirectoryOnly() {
        // filename comes from MessageGeneratorCommand.java
        File checkPath = new File("002/pacs008_0_0.xml");

        int exitCode = new CommandLine(command, cliFactory).execute("-n=1", "-d=002");

        // assertEquals("Foo", outputStreamCaptor.toString());
        assertEquals(0, exitCode);
        assertTrue(checkPath.isFile());
        FileUtils.deleteDirectory(new File("002"));
    }

    @Test
    @Order(3)
    public void testCommandDirectory_file() {
        File checkPath = new File("003/test_0_0.xml");

        int exitCode = new CommandLine(command, cliFactory).execute("-n=1", "-d=003", "-o=test_");

        assertEquals(0, exitCode);
        assertTrue(checkPath.isFile());
        FileUtils.deleteDirectory(new File("003"));
    }

    @Test
    @Order(4)
    public void testCommandZipFlagWithFilePrefix() {
        DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        CommandLine cmd = new CommandLine(command);

        int exitCode = cmd.execute( "-n=3", "-d=005", "-o=test", "-z");
        File checkPath = new File(String.format("005/test-%s.zip", timeStampPattern.format(java.time.LocalDateTime.now())));

        assertEquals(0, exitCode);
        assertTrue(checkPath.isFile());

        FileUtils.deleteDirectory(new File("005"));
    }

    // Could not get this last test to work. It almost seems like the arguments from previous unit tests carry over
    // For example the fact that any test had a -o option now means there isn't any way not to specify it.
//    @Test
//    @Order(5)
//    public void test_command_zip_flag_without_file_prefix() {
//        DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//
//        int exitCode = new CommandLine(command, cliFactory).execute("-n=3", "-d=004", "--archive");
//        File checkPath = new File(String.format("004/%s.zip", timeStampPattern.format(java.time.LocalDateTime.now())));
//
//        assertEquals(0, exitCode);
//        assertTrue(checkPath.isFile());
//        Common.deleteDirectory(new File("004"));
//    }

    @Test
    @Order(1)
    public void testCommandNoDirectoryNoFile() throws IOException {
        // file name set in MessageGeneratorCommand
        File checkPath = new File("pacs008_0_0.xml");

        int exitCode = new CommandLine(command, cliFactory).execute("-n=1");

        assertEquals(0, exitCode);
        assertTrue(checkPath.isFile());
        deleteIfExists(Path.of("pacs008_0_0.xml"));
    }
}
