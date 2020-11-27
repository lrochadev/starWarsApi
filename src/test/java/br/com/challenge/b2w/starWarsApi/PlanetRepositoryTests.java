package br.com.challenge.b2w.starWarsApi;

import br.com.challenge.b2w.starWarsApi.model.Planet;
import br.com.challenge.b2w.starWarsApi.repository.PlanetRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * @author Leonardo Rocha
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class PlanetRepositoryTests {

    @Autowired
    private PlanetRepository planetRepository;

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
        Planet planet = new Planet(null, "Alderaan", "temperate", "mountains", 1);
        planet = this.planetRepository.save(planet);

        assertThat(planet.getId()).isNotNull();
        assertThat(planet.getName()).isEqualTo("Alderaan");
        assertThat(planet.getClimate()).isEqualTo("temperate");
        assertThat(planet.getTerrain()).isEqualTo("mountains");
        assertThat(planet.getQuantityOfApparitionInMovies()).isEqualTo(1);
    }

    @Test
    public void deleteShouldRemoveData() {
        Planet planet = new Planet(null, "Tatooine", "temperate", "mountains", 0);
        planet = this.planetRepository.save(planet);

        planetRepository.delete(planet);

        assertThat(planetRepository.findById(planet.getId())).isEmpty();
    }

    @Test
    public void updateShouldChangeAndPersistData() {
        Planet planet = new Planet(null, "Naboo", "tropical", "mountains", 1);
        planet = this.planetRepository.save(planet);

        planet.setName("Endor");
        planet.setClimate("temperate");
        Planet planetUpdated = this.planetRepository.save(planet);

        Optional<Planet> planetDb = this.planetRepository.findById(planet.getId());
        assertNotNull(planetDb);
        assertThat(planetUpdated.getName()).isEqualTo("Endor");
    }

    @Test
    public void findByNameIgnoreCaseContainingShouldIgnoreCase() {

        this.planetRepository.saveAll(asList(
                new Planet(null, "Alderaan", "temperate", "mountains", 0),
                new Planet(null, "alderaan", "tropical", "mountains", 0)
        ));

        planetRepository.findByNameIgnoreCaseContaining("alderaan").ifPresent(planets -> assertThat(planets.size()).isEqualTo(2));
    }

    @Test
    public void createWhenNameIsNullShouldThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        this.planetRepository.save(null);
    }

    private void clearDb() {
        this.planetRepository.deleteAll();
    }
}
