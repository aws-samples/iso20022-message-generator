/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import rapide.iso20022.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipInputStream;

import static java.nio.file.Files.deleteIfExists;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertFalse;

@Slf4j
@ActiveProfiles("unittest")
public class FileUtilsTest {
    @Before("")
    public void setup() throws Exception {

    }

    @Test
    @Order(1)
    public void test_addMessageFilesToZipFile_files() throws IOException {
        Path[] paths = {
                Path.of("001/iso20022-0.xml"),
                Path.of("001/iso20022-1.xml"),
                Path.of("001/iso20022-2.xml")
        };
        String zipFileName = "001/archive101.zip";

        int i = 0;
        for (Path p: paths) {
            FileUtils.writeMessageToFile(String.format("%d The quick brown fox jumped over the lazy dog.", i), p.toString());
            i++;
        }

        FileUtils.addMessageFilesToZipFile(new ArrayList<Path>(Arrays.asList(paths)), zipFileName);

        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFileName));

        try {
            assertThat(zin.getNextEntry().getName().toString()).isEqualTo("001/iso20022-0.xml");
            assertThat(zin.getNextEntry().getName().toString()).isEqualTo("001/iso20022-1.xml");
            assertThat(zin.getNextEntry().getName().toString()).isEqualTo("001/iso20022-2.xml");
        } finally {
            FileUtils.deleteDirectory(new File("001"));
        }
    }

    @Test
    @Order(2)
    public void test_writeMessageFile_with_directory() throws IOException {
        FileUtils.writeMessageToFile("test", "010/testWriteMessageFile.xml");
        List<String> lines = Files.readAllLines(Path.of("010/testWriteMessageFile.xml"));
        try {
            assertThat(lines.get(0)).isEqualTo("test");
        } finally {
            FileUtils.deleteDirectory(new File("010"));
        }

    }

    @Test
    @Order(3)
    public void test_writeMessageFile_without_directory() throws IOException {
        FileUtils.writeMessageToFile("test", "testWriteMessageFile.xml");
        List<String> lines = Files.readAllLines(Path.of("testWriteMessageFile.xml"));
        try {
            assertThat(lines.get(0)).isEqualTo("test");
        } finally {
            deleteIfExists(Path.of("testWriteMessageFile.xml"));
        }
    }

    @Test
    public void givenDirectory_whenDeletedWithRecursion_thenIsGone()
            throws IOException {
        Files.createDirectory(Path.of("common_foo"));
        Files.createFile(Path.of("common_foo/pumpkin"));
        Files.createFile(Path.of("common_foo/squash"));
        Path pathToBeDeleted = Path.of("common_foo");

        boolean result = FileUtils.deleteDirectory(pathToBeDeleted.toFile());

        assertTrue(result);
        assertFalse(
                "Directory still exists",
                Files.exists(pathToBeDeleted));
    }
}
