package rapide.iso20022.cli.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@ActiveProfiles("unittest")
public class ConfigPropertiesTest {
    @Autowired
    private ConfigProperties configProperties;

    @Test
    public void loadRapidePurposeCode() {
        assertThat(configProperties.getPurposeCode()).isEqualTo(List.of(
                "BONU",
                "CASH",
                "CBLK",
                "CCRD",
                "CORT",
                "DCRD",
                "DIVI",
                "DVPM",
                "EPAY",
                "FCIN",
                "FCOL",
                "GOVT",
                "HEDG",
                "ICCP",
                "IDCP",
                "INTC",
                "INTE",
                "LOAN",
                "MP2B",
                "MP2P",
                "OTHR",
                "PENS",
                "RPRE",
                "RRCT",
                "RVPM",
                "SALA",
                "SECU",
                "SSBE",
                "SUPP",
                "TAXS",
                "TRAD",
                "TREA",
                "VATX",
                "WHLD"));
    }

    @Test
    public void loadRapideCurrency() {
        assertThat(configProperties.getCurrency().get(0)).isEqualTo(Map.of("Country", "US", "Code", "USD"));
    }
}
