package br.com.desafio.b2w.starWarsApi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.repository.PlanetaRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanetaRepositoryTests {

	@Autowired
	private PlanetaRepository planetaRepository;

	@Before
	public void setUp() throws Exception {

		Planeta planeta = new Planeta(null, "Alderaan", "", "mountains", 0);
		
		assertNull(planeta.getId());
		assertNotNull(planeta.getNome());
		assertNotNull(planeta.getClima());
		assertNotNull(planeta.getTerreno());

		this.planetaRepository.save(planeta);

		assertNotNull(planeta.getId());
	}
	
	@After
	public void clearDatabase() {
		this.planetaRepository.deleteAll();
	}
	
	@Test
	public void findAllPlanets() throws IOException, Exception {
		assertNotNull(planetaRepository.findAll());
	}
}
