package br.com.challenge.b2w.starWarsApi.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class RestClientPropertiesTest {

    @Autowired
    private RetryMessageProperties props;

    @Test
    void allPropertiesAreBound() {
        assertThat(props.getConnectionRequestTimeout()).isEqualTo(1000);
        assertThat(props.getConnectTimeout()).isEqualTo(5000);
        assertThat(props.getSocketTimeout()).isEqualTo(1000);
        assertThat(props.getMaxConnections()).isEqualTo(20);
        assertThat(props.getMaxPerRoute()).isEqualTo(6);
        assertThat(props.getValidateAfterInactivity()).isEqualTo(1);
        assertThat(props.getRetryCount()).isEqualTo(1);
        assertThat(props.getRetrySleepTimeMS()).isEqualTo(20);
    }
}
