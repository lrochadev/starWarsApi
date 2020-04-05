package br.com.desafio.b2w.starWarsApi;

import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.repository.PlanetaRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpMethod.DELETE;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
public class PlanetaResourceTests {

	@MockBean
	private PlanetaRepository planetaRepository;
	
    @Autowired
    private TestRestTemplate restTemplate;
    
    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder();
        }
    }

	@Before
	public void setUp() {
		Optional<Planeta> planeta = Optional.of(new Planeta("1", "Alderaan", "temperate", "mountains", 0));
        BDDMockito.when(planetaRepository.findById(planeta.get().getId())).thenReturn(planeta);
	}
	
    @Test
    public void listPlanetasShouldReturnStatusCode200() {
        List<Planeta> planetas = asList(new Planeta("1", "Endor", "tropical", "mountains", 0), new Planeta("2", "Aragorn", "tropical", "mountains", 0));
        BDDMockito.when(planetaRepository.findAll()).thenReturn(planetas);
        ResponseEntity<String> response = restTemplate.getForEntity("/api/planetas/", String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    
    @Test
    public void getPlanetaByIdWhenIdAreCorrectShouldReturnStatusCode200() {
        ResponseEntity<Planeta> response = restTemplate.getForEntity("/api/planetas/{id}", Planeta.class, "1");
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getPlanetaByIdWhenPlanetaDoesNotExistShouldReturnStatusCode404() {
        ResponseEntity<Planeta> response = restTemplate.getForEntity("/api/planetas/{id}", Planeta.class, "-1");
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deleteWhenIdExistsShouldReturnStatusCode200() {
        BDDMockito.doNothing().when(planetaRepository).deleteById("1");
        ResponseEntity<String> exchange = restTemplate.exchange("/api/planetas/{id}", DELETE, null, String.class, "1");
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(204);
    }

    @Ignore
    public void createWhenNameIsNullShouldReturnStatusCode400BadRequest() {
        Planeta planeta = new Planeta("3", null, "tropical", "mountains", 0);
        BDDMockito.when(planetaRepository.save(planeta)).thenReturn(planeta);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/planetas/", planeta, String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }
    
    @Test
    public void createShouldPersistDataAndReturnStatusCode201() {
        Planeta planeta = new Planeta("3", "Naboo", "tropical", "mountains", 0);
        BDDMockito.when(planetaRepository.save(planeta)).thenReturn(planeta);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/planetas/", planeta, String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }
}
