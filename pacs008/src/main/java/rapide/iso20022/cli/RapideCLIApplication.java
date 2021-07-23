/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.cli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@Slf4j
@EnableJpaRepositories(basePackages = {"rapide.iso20022.data.lei.repository", "rapide.iso20022.data.bic.repository"})
@EntityScan(basePackages = {"rapide.iso20022.data.lei.model", "rapide.iso20022.data.bic.model"})
@SpringBootApplication
public class RapideCLIApplication implements CommandLineRunner, ExitCodeGenerator {
    private MessageGeneratorCommand command;
    private IFactory cliFactory;
    private int exitCode;

    // Constructor based DI
    public RapideCLIApplication(IFactory cliFactory, MessageGeneratorCommand command) {
        this.cliFactory = cliFactory;
        this.command = command;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(RapideCLIApplication.class, args)));
    }

    @Override
    public void run(String... args) {
        log.info("EXECUTING : command line runner");
        for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
        }
        exitCode = new CommandLine(command, cliFactory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
