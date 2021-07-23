/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtils {
    // code provided by https://www.baeldung.com/java-delete-directory
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void writeMessageToFile(String message, String fileName) throws IOException
    {
        // create directory if there is a parent path and it doesn't exist
        File f = new File(fileName);
        if (f.getParent() != null) {
            File d = new File(f.getParent());
            if (! d.exists()) {
                Files.createDirectory(Path.of(f.getParent()));
            }
        }

        try (FileWriter fw = new FileWriter(fileName, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fw))
        {
            writer.write(message);
        }
    }

    public static void addMessageFilesToZipFile(List<Path> fileNames, String archiveName) throws IOException
    {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archiveName, true))) {
            byte[] buffer = new byte[4096];

            for (Path p: fileNames) {
                try (FileInputStream fis = new FileInputStream(new File(p.toString()));)
                {
                    zos.putNextEntry(new ZipEntry(p.toString()));

                    // Read byte array buffer and write contents to ZipOutputStream
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }

        }
    }
}
