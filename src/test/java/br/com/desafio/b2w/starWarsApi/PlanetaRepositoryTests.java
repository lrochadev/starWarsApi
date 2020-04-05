package br.com.desafio.b2w.starWarsApi;

import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.repository.PlanetaRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@RunWith(SpringRunner.class)
@DataMongoTest
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
		planeta = this.planetaRepository.save(planeta);

		assertThat(planeta.getId()).isNotNull();
		assertThat(planeta.getNome()).isEqualTo("Alderaan");
		assertThat(planeta.getClima()).isEqualTo("temperate");
		assertThat(planeta.getTerreno()).isEqualTo("mountains");
		assertThat(planeta.getQtdAparicoesEmFilmes()).isEqualTo(1);
	}

	@Test
	public void deleteShouldRemoveData() {
		Planeta planeta = new Planeta(null, "Tatooine", "temperate", "mountains", 0);
		planeta = this.planetaRepository.save(planeta);

		planetaRepository.delete(planeta);

		assertThat(planetaRepository.findById(planeta.getId())).isEmpty();
	}

	@Test
	public void updateShouldChangeAndPersistData() {
		Planeta planeta = new Planeta(null, "Naboo", "tropical", "mountains", 1);
		planeta = this.planetaRepository.save(planeta);

		planeta.setNome("Endor");
		planeta.setClima("temperate");
		Planeta planetaAtualizado = this.planetaRepository.save(planeta);

		Optional<Planeta> planetaDb = this.planetaRepository.findById(planeta.getId());
		assertNotNull(planetaDb);
		assertThat(planetaAtualizado.getNome()).isEqualTo("Endor");
	}

	@Test
	public void findByNameIgnoreCaseContainingShouldIgnoreCase() {

		this.planetaRepository.saveAll(asList(
				new Planeta(null, "Alderaan", "temperate", "mountains", 0),
				new Planeta(null, "alderaan", "tropical", "mountains", 0)
		));
		
		planetaRepository.findByNomeIgnoreCaseContaining("alderaan").ifPresent(planetas -> assertThat(planetas.size()).isEqualTo(2));
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
