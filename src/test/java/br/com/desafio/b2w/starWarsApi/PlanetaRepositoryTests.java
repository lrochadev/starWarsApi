package br.com.desafio.b2w.starWarsApi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.repository.PlanetaRepository;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanetaRepositoryTests {
	
	@Autowired
	private PlanetaRepository planetaRepository;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void clearBeforeInit() {
		clearDb();
	}
	
	@After
	public void clearAfterInit() {
		clearDb();
	}

	@Test
	public void createShouldPersistData() {
		Planeta planeta = new Planeta(null, "Alderaan", "temperate", "mountains", 1);
		this.planetaRepository.save(planeta);
		assertThat(planeta.getId()).isNotNull();
		assertThat(planeta.getNome()).isEqualTo("Alderaan");
		assertThat(planeta.getClima()).isEqualTo("temperate");
		assertThat(planeta.getTerreno()).isEqualTo("mountains");
		assertThat(planeta.getQtdAparicoesEmFilmes()).isEqualTo(1);
	}

	@Test
	public void deleteShouldRemoveData() {
		Planeta planeta = new Planeta(null, "Tatooine", "temperate", "mountains", 0);
		this.planetaRepository.save(planeta);
		planetaRepository.delete(planeta);
		assertThat(planetaRepository.findById(planeta.getId())).isEmpty();
	}

	@Test
	public void updateShouldChangeAndPersistData() {
		Planeta planeta = new Planeta(null, "Naboo", "tropical", "mountains", 1);
		this.planetaRepository.save(planeta);
		
		planeta.setNome("Endor");
		planeta.setClima("temperate");
		planeta = this.planetaRepository.save(planeta);
		
		Optional<Planeta> planetaDb = this.planetaRepository.findById(planeta.getId());
		assertNotNull(planetaDb);
		assertThat(planeta.getNome()).isEqualTo("Endor");
	}

	@Test
	public void findByNameIgnoreCaseContainingShouldIgnoreCase() {
		Planeta planetOne = new Planeta(null, "Alderaan", "temperate", "mountains", 0);
		Planeta planetTwo = new Planeta(null, "alderaan", "tropical", "mountains", 0);
		
		this.planetaRepository.save(planetOne);
		this.planetaRepository.save(planetTwo);
		
		Optional<List<Planeta>> planetas = planetaRepository.findByNomeIgnoreCaseContaining("alderaan");

		if (planetas.isPresent()) {
			assertThat(planetas.get().size()).isEqualTo(2);
		}
	}

	@Test
	public void createWhenNameIsNullShouldThrowIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		this.planetaRepository.save(null);
	}
	
	private void clearDb() {
		this.planetaRepository.deleteAll();
	}
}
