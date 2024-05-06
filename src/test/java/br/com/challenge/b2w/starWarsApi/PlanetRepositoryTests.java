package br.com.challenge.b2w.starWarsApi;

import br.com.challenge.b2w.starWarsApi.model.Planet;
import br.com.challenge.b2w.starWarsApi.repository.PlanetRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Leonardo Rocha
 */
@SpringBootTest
class PlanetRepositoryTests extends ContainerBase {

    @Autowired
    private PlanetRepository planetRepository;

    @BeforeEach
    public void clearBeforeInit() {
        clearDb();
    }

    @AfterEach
    public void clearAfterInit() {
        clearDb();
    }

    @Test
    void createShouldPersistData() {
        Planet planet = new Planet(null, "Alderaan", "temperate", "mountains", 1);
        planet = this.planetRepository.save(planet);

        assertThat(planet.getId()).isNotNull();
        assertThat(planet.getName()).isEqualTo("Alderaan");
        assertThat(planet.getClimate()).isEqualTo("temperate");
        assertThat(planet.getTerrain()).isEqualTo("mountains");
        assertThat(planet.getQuantityOfApparitionInMovies()).isEqualTo(1);
    }

    @Test
    void deleteShouldRemoveData() {
        Planet planet = new Planet(null, "Tatooine", "temperate", "mountains", 0);
        planet = this.planetRepository.save(planet);

        planetRepository.delete(planet);

        assertThat(planetRepository.findById(planet.getId())).isEmpty();
    }

    @Test
    void updateShouldChangeAndPersistData() {
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
    void findByNameIgnoreCaseContainingShouldIgnoreCase() {

        this.planetRepository.saveAll(asList(
                new Planet(null, "Alderaan", "temperate", "mountains", 0),
                new Planet(null, "alderaan", "tropical", "mountains", 0)
        ));

        planetRepository.findByNameIgnoreCaseContaining("alderaan").ifPresent(planets -> assertThat(planets).hasSize(2));

    }

    @Test
    void createWhenNameIsNullShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.planetRepository.save(null));
    }

    private void clearDb() {
        this.planetRepository.deleteAll();
    }
}
