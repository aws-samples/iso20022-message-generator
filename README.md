## rapide - An ISO 20022 Message Generator
This project is a simple ISO20022 message generator that generates pacs.008 xml messages for serial message flow. The 
project is called [rapide](https://dictionary.cambridge.org/dictionary/french-english/rapide), a French word. It translates to 
[swift or fast or quick](https://dictionary.cambridge.org/dictionary/english-french/swift) or swift, a swallow like bird. 

The Java package names start with rapide at the root. You will notice references to rapide in different places such as 
name of the wrapper bash shell script for the CLI tool.

## Getting Started
### Prerequsities
The rapide software is built using Java. You will need JDK 11 or higher to be installed on your developer machine. You 
can use [Amazon Corretto](https://aws.amazon.com/corretto/), an openJDK distribution from Amazon.

### Build the Message Generator 
1. Clone this repository:
   ```bash
   git clone https://github.com/aws-samples/iso20022-message-generator.git
   ```

1. Change directory to the clone repository:
   ```bash
   cd iso20022-message-generator
   ```
   
1. Build the message generator:
   ```bash
   ./gradlew clean build
   ```

   This will build the Spring Boot jar.  
  
1. Run the iso20022 message generator cli:
   ```bash
   cd pacs008
   ./rapide-iso20022 # this print the command line options
   ```
   

## Usage
rapide, an ISO 20022 message generator, is command line tool and provides options to control:  
- number of sample messages to generate
- customize name of the generated xml message file
- directory where to place all generated xml message files
- zip all generated xml message files

```bash 
Usage: rapide-iso20022 [-z] [-d=<outputDirectory>] -n=<numberOfMessages> [-o=<outputFilePrefix>]
    -d, --directory=<outputDirectory>
          Directory where files will be written to
    -n, --number-of-messages=<numberOfMessages>
          Number of messages to generate
    -o, --output-file-prefix=<outputFilePrefix>
          File name prefix that will store generated messages
    -z, --archive   Flag to create zip file of generated files
```


## Architecture
Rapide software is built using Java and it uses following open source Java libraries, frameworks and platforms:
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Gradle Build Tool](https://gradle.org/)
- [Hibernate ORM](https://hibernate.org/orm/)
- [Project Lombok](https://projectlombok.org/)  
- [Prowide iso20022 library](https://github.com/prowide/prowide-iso20022)

### Databases
There two databases used by the message generator:  
- LEI Database
- BIC Database
  
The `rapide.iso20022.data` package has Spring Data Repository and JPA Entities for each database. 
- [LegalEntity](pacs008/src/main/java/rapide/iso20022/data/lei/model/LegalEntity.java) - see this class for LegalEntity attributes
- [BICRecord](pacs008/src/main/java/rapide/iso20022/data/bic/model/BICRecord.java) - see this class for BIC attributes
- [Database Schema](pacs008/src/main/resources/schema.sql) has DDL for above entities as well code for loading H2 in-memory 
  database with records from a CSV files. 
  
## Configuring the Message Generator
The ISO 20022 message generator CLI tool provides various options to customize message generation. As mentioned the message generator 
tool built using Spring Boot and hence the customizations are done using Spring Boot's `application.yaml` file. 
These options can be provided using [Spring Boot's mechanisms for providing application yaml](
https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config), see section 2.3:

1. Modifying the [application.yaml](pacs008/src/main/resources/application.yml) file and rebuilding the jar.
1. Providing an updated `application.yaml` in current directory (i.e. pacs008) or config directory in pacs008d directory. 
   Spring Boot will automatically read `application.yaml` placed above directory.

The `application.yaml` file contains configurable properties for both Spring Boot related resources and rapide CLI tool. 
The Spring related configuration properties are under Spring's namespace and hierarchy.

The rapide's message generator's configurable properties are under `rapide` hierarchy:
```yaml
rapide:
  PurposeCode:
  Currency:
  SourceBicList:
  DestinationBicList:
  ...
```

### Using a Relational Database Server
You can replace in-memory H2 relational database with any relational database such as Amazon RDS (any support relational 
database e.g. mysql, postgresql et.c). This is possible due to Spring Boot's [Spring Data JPA](
https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.sql.jpa-and-spring-data) support.

The data source related properties in `application.yaml` can be configured to meet the needs of your database platform. 
See Spring Boot's documentation on [Data Access](
https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-access). For example to use Amazon RDS 
for MySQL, the application.yaml file will look like one shown below:
```yaml
spring:
  datasource:
    platform: mysql
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://{instance-id}.{random-identifier}.{region}.rds.amazonaws.com:3306/{database-name}
    username: <username>
    password: <password>
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
```

You can also initialize database with reference data or full records on startup using Hibernate's database initialization mechanism in 
conjunction with Spring Boot as documented [here](
https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization).

### Category and Payment Purpose Codes
The category codes and payment purpose codes used in the generated messages can be customized by providing them in `application.yaml` 
under property `rapide.PurposeCode`:  
```yaml
rapide:
  PurposeCode:
    - Your code 1
    - Your code 2
    - ... 
```

### Currency Codes
You can provide currency for countries by adding them in the `application.yaml` under `rapide.Currency` property:  
```yaml
   rapide:
   Currency:
      - Country: US
        Code: USD
      - Country: UK
        Code: GBP
```

### Source and Destination BICs
You can add a list of source and destination BICs to be used in generated messages by adding them in the `application.yaml` 
under `rapide.SourceBicList` property for source BIC list (from BIC) and `rapide.DestinationBicList` property for 
destination BIC list:
```yaml
   rapide:
      SourceBicList:
         - Your Source BIC1
         - Your Source BIC2
      DestinationBicList:
         - Your Destination BIC1
         - Your Destination BIC2
```   

In this case the BIC codes in BIC database are ignored (i.e. are not selected randomly from BIC database). You can choose to 
just override source BIC or destination BIC only or both.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.

