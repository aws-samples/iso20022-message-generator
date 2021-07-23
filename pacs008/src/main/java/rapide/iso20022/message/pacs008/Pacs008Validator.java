/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.pacs008;

import com.prowidesoftware.swift.model.mx.AbstractMX;
import lombok.extern.slf4j.Slf4j;
import rapide.iso20022.cli.config.ConfigProperties;
import rapide.iso20022.message.generators.ValidatorResponse;
import rapide.iso20022.message.generators.interfaces.IValidator;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class Pacs008Validator implements IValidator {

    private ConfigProperties configProperties;
    private ValidatorResponse validationResponse;

    public void setConfigProperties(ConfigProperties configProperties)
    {
        this.configProperties=configProperties;
    }

    @Override
    public ValidatorResponse validate(AbstractMX message) {
        validationResponse = new ValidatorResponse();

        validate(message.document().toString() ,getPacs008XSD());
        return validationResponse;
    }

    private String getPacs008XSD() {
        List<HashMap<String, String>> schemas = configProperties.getValidationSchema();
         String result = null;
        for (HashMap<String, String> s: schemas) {
            String message = s.get("Message");
            if (message.equals("800108.0"))
                result = s.get("FileName");
        }
        log.info(result);
        return result;
    }

    private void validate(String xmlString, String schemaFile) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {

            URL xsdResource = getClass().getClassLoader().getResource(schemaFile);
            Schema schema = schemaFactory.newSchema(xsdResource);

            Validator validator = schema.newValidator();
            //validator.validate(new StreamSource(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))));
            validator.validate(new StreamSource(new StringReader(xmlString)));
            validationResponse.setValid(true);

        } catch (SAXException | IOException e) {
            //e.printStackTrace();
            validationResponse.setValidationResults(e.getMessage());
            validationResponse.setValid(false);
        }
    }

}
