package br.com.desafio.b2w.starWarsApi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.repository.PlanetaRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanetaResourceTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private PlanetaRepository planetaRepository;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		planetaRepository.save(new Planeta(null, "Alderaan", "temperate", "mountains", 0));
	}

	@Test
	public void createPlanetAndVerifyIfContained() throws IOException, Exception {

		Planeta planeta = new Planeta(null, "Yavin IV", "tropical", "jungle", 0);

		mockMvc.perform(post("/api/planetas").contentType(MediaType.APPLICATION_JSON).content(toJson(planeta)));

		List<Planeta> found = planetaRepository.findAll();

		assertThat(found).extracting(Planeta::getNome).contains("Yavin IV");
	}

	@Test
	public void getPlanetasWithStatus200() throws Exception {

		createPlanet("PlanetOne");

		createPlanet("PlanetTwo");

		mockMvc.perform(get("/api/planetas").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
//				.andExpect(jsonPath("$[0].name", is(equalTo("Alderaan"))));
	}

	private byte[] toJson(Object object) throws IOException {
		return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsBytes(object);
	}

	private void createPlanet(String nomePlaneta) {
		this.planetaRepository.save(new Planeta(null, nomePlaneta, "tropical", "mountains", 0));
	}

}
