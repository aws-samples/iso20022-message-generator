/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.cli;

import com.prowidesoftware.swift.model.mx.AbstractMX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import rapide.iso20022.cli.config.ConfigProperties;
import rapide.iso20022.util.FileUtils;
import rapide.iso20022.message.generators.Pacs008Generator;
import rapide.iso20022.message.generators.ValidatorResponse;
import rapide.iso20022.message.pacs008.Pacs008Validator;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@Component
@Command(name = "rapide-iso20022")
public class MessageGeneratorCommand implements Callable<Integer> {

    @Autowired
    private Pacs008Generator pacs008Generator;

    @Autowired
    private Pacs008Validator pacs008Validator;

    @Autowired
    private ConfigProperties configProperties;

    @CommandLine.Option(names = {"-n", "--number-of-messages"}, description = "Number of messages to generate", required = true)
    private int numberOfMessages;

    @CommandLine.Option(names = {"-o", "--output-file-prefix"}, description = "File name prefix that will store generated messages", required = false)
    private String outputFilePrefix;

    @CommandLine.Option(names = {"-d", "--directory"}, description = "Directory where files will be written to", required = false)
    private String outputDirectory;

    @CommandLine.Option(names = {"-z", "--archive"}, description = "Flag to create zip file of generated files", required = false)
    private boolean isArchive;

    private String returnPath(String directory, String file) {
        if (directory != null && file != null) {
            return Paths.get(directory, file).toString();
        } else if (directory == null && file != null) {
            return file;
        } else if (directory != null && file == null)
            return directory + File.separator;
        return "";
    }

    public Integer call() throws Exception {
        String fileName;
        List<Path> filePaths = new ArrayList<>();
        DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        log.info("Options => numberOfMessage: {}, outputFile: {}, outputDirectory {}", numberOfMessages, outputFilePrefix, outputDirectory);
        log.info("Generating messages started");

        int maxRetry = configProperties.getInvalidMessageRetry();
        String prefixPath = returnPath(outputDirectory, outputFilePrefix);
        log.info("Max retry is set to:" + maxRetry);
        for (int i = 0; i < numberOfMessages; i++) {
            log.info(String.format("Generating message %d", i));
            boolean generate = true;
            int retryCounter = 1;
            List<AbstractMX> messages;
            while (generate && retryCounter <= maxRetry) {
                messages = pacs008Generator.generate();
                boolean invalidMessage = false;

                for (int j = 0; j < messages.size(); j++) {
                    AbstractMX mx = messages.get(j);
                    //log.info(mx.document());
                    ValidatorResponse result = pacs008Validator.validate(mx);
                    if (!result.isValid()) {
                        System.out.println("Invalid message. " + result.getValidationResults());
                        log.info("Invalid message. " + result.getValidationResults());
                        invalidMessage = true;
                    }
                    if (invalidMessage) break;
                }

                if (invalidMessage) {
                    retryCounter++;
                    if (retryCounter == maxRetry)
                        throw new RuntimeException("Max message generator retry exceeded!");
                    log.info("Regenerate message attempt #" + retryCounter);
                } else {
                    generate = false;
                    filePaths.add(saveMessages(messages, prefixPath, i));
                }
            }

        }

        log.info("filePaths: " + filePaths);

        if (isArchive) {
            String archiveFileName;
            if (outputFilePrefix != null) {
                archiveFileName = String.format("%s-%s.zip", outputFilePrefix, timeStampPattern.format(java.time.LocalDateTime.now()));
            } else {
                archiveFileName = String.format("%s.zip", timeStampPattern.format(java.time.LocalDateTime.now()));
            }
            String archiveFilePath = returnPath(outputDirectory, archiveFileName);
            FileUtils.addMessageFilesToZipFile(filePaths, archiveFilePath);
        }

        // System.out.println("File generated. please see files pacs008_x_x.xml");
        log.info("Generating messages end");
        return 0;
    }

    private Path saveMessages(List<AbstractMX> messages, String prefixPath, int parentMessageID) throws IOException {
        Path filePath;
        // Save the messages to the disk
        String fileName;
        for (int m=0;m<messages.size();m++) {
            log.info("prefixPath {}", prefixPath);
            if (prefixPath.isEmpty()) {
                fileName = "pacs008_" + parentMessageID + "_" + m + ".xml";
            } else if (prefixPath.endsWith(File.separator)) {
                fileName = prefixPath + "pacs008_" + parentMessageID + "_" + m + ".xml";
            } else {
                fileName = prefixPath + parentMessageID + "_" + m + ".xml";
            }

            log.info("Output file - " + fileName);
            FileUtils.writeMessageToFile(messages.get(m).message(), fileName);
            log.info("SaveMessages filepath: " + fileName);
            return Path.of(fileName);
        }


        return Path.of("");
        // System.out.println("File generated. please see files pacs008_x_x.xml");
    }

}
