/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
DROP TABLE IF EXISTS LEI;
DROP TABLE IF EXISTS BIC;
CREATE TABLE LEI(ID varchar(255) PRIMARY KEY,
                 legal_name VARCHAR(512),
                 legal_name_lang VARCHAR(255),
                 legal_address_lang VARCHAR(255),
                 legal_address_first_address_line VARCHAR(255),
                 legal_address_additional_address_line1 VARCHAR(255),
                 legal_address_additional_address_line2 VARCHAR(255),
                 legal_address_city VARCHAR(255),
                 legal_address_region VARCHAR(255),
                 legal_address_country VARCHAR(255),
                 legal_address_postal_code VARCHAR(255),
                 headquarters_address_lang VARCHAR(255),
                 headquarters_address_first_address_line VARCHAR(255),
                 headquarters_address_additional_address_line1 VARCHAR(255),
                 headquarters_address_additional_address_line2 VARCHAR(255),
                 headquarters_address_city VARCHAR(255),
                 headquarters_address_region VARCHAR(255),
                 headquarters_address_country VARCHAR(255),
                 headquarters_address_postal_code VARCHAR(255))
AS SELECT
    LEI as ID,
    "Entity.LegalName" as legal_name,
    "Entity.LegalName.xmllang" as legal_name_lang,
    "Entity.LegalAddress.xmllang" as legal_address_lang,
    "Entity.LegalAddress.FirstAddressLine" as legal_address_first_address_line,
    "Entity.LegalAddress.AdditionalAddressLine.1" as legal_address_additional_address_line1,
    "Entity.LegalAddress.AdditionalAddressLine.2" as legal_address_additional_address_line2,
    "Entity.LegalAddress.City" as legal_address_city,
    "Entity.LegalAddress.Region" as legal_address_region,
    "Entity.LegalAddress.Country" as legal_address_country,
    "Entity.LegalAddress.PostalCode" as legal_address_postal_code,
    "Entity.HeadquartersAddress.xmllang" as headquarters_address_lang,
    "Entity.HeadquartersAddress.FirstAddressLine" as headquarters_address_first_address_line,
    "Entity.HeadquartersAddress.AdditionalAddressLine.1" as headquarters_address_additional_address_line1,
    "Entity.HeadquartersAddress.AdditionalAddressLine.2" as headquarters_address_additional_address_line2,
    "Entity.HeadquartersAddress.City" as headquarters_address_city,
    "Entity.HeadquartersAddress.Region" as headquarters_address_region,
    "Entity.HeadquartersAddress.Country" as headquarters_address_country,
    "Entity.HeadquartersAddress.PostalCode" as headquarters_address_postal_code
--   FROM CSVREAD('classpath:db/lei_sample_records.csv', NULL, 'fieldDelimiter="');
    FROM CSVREAD('classpath:db/lei_prototype_records.csv', NULL, 'fieldDelimiter="');

CREATE TABLE BIC(ID varchar(255) PRIMARY KEY,
                 BIC_code VARCHAR(512),
                 branch_code VARCHAR(255),
                 institution_name VARCHAR(255),
                 city_heading VARCHAR(255),
                 physical_address1 VARCHAR(255),
                 physical_address2 VARCHAR(255),
                 physical_address3 VARCHAR(255),
                 location VARCHAR(255),
                 country_name VARCHAR(255),
                 country_code VARCHAR(255))
AS SELECT
       CONCAT("BIC CODE", '-', "BRANCH CODE") as ID,
       "BIC CODE" as BIC_code,
       "BRANCH CODE" as branch_code,
       "INSTITUTION NAME" as institution_name,
       "CITY HEADING" as city_heading,
       "PHYSICAL ADDRESS 1" as physical_address1,
       "PHYSICAL ADDRESS 2" as physical_address2,
       "PHYSICAL ADDRESS 3" as physical_address3,
       "LOCATION" as location,
       "COUNTRY NAME" as country_name,
       SUBSTRING("BIC CODE" FROM 5 FOR 2) as country_code
--    FROM CSVREAD('classpath:db/bic_sample_records.csv', NULL, 'fieldDelimiter="');
       FROM CSVREAD('classpath:db/bic_fake_records.csv', NULL, 'fieldDelimiter="');