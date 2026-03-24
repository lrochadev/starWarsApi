package br.com.challenge.b2w.starWarsApi;

import br.com.challenge.b2w.starWarsApi.dto.swapi.PropertiesDto;
import br.com.challenge.b2w.starWarsApi.dto.swapi.SwapiDto;
import br.com.challenge.b2w.starWarsApi.exception.SWAPIException;
import br.com.challenge.b2w.starWarsApi.services.SwapiService;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class SwapiServiceTests extends ContainerBase {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("starwars.api.url", () -> wireMock.baseUrl() + "/api/planets?search=");
    }

    @Autowired
    private SwapiService swapiService;

    @Test
    void consultSwAPI_shouldReturnPlanetData_whenSwapiRespondsSuccessfully() {
        wireMock.stubFor(get(urlPathEqualTo("/api/planets"))
                .withQueryParam("search", equalTo("Tatooine"))
                .willReturn(okJson("""
                        {"results":[{"name":"Tatooine","films":["film1","film2","film3"]}]}
                        """)));

        SwapiDto result = swapiService.consultSwAPI("Tatooine");

        assertThat(result).isNotNull();
        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults().getFirst().getName()).isEqualTo("Tatooine");
        assertThat(result.getResults().getFirst().getFilms()).hasSize(3);
    }

    @Test
    void consultSwAPI_shouldReturnEmptyResults_whenPlanetNotFoundInSwapi() {
        wireMock.stubFor(get(urlPathEqualTo("/api/planets"))
                .withQueryParam("search", equalTo("UnknownPlanet999"))
                .willReturn(okJson("""
                        {"results":[]}
                        """)));

        SwapiDto result = swapiService.consultSwAPI("UnknownPlanet999");

        assertThat(result).isNotNull();
        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void consultSwAPI_shouldThrowSWAPIException_whenServerReturns500() {
        wireMock.stubFor(get(urlPathEqualTo("/api/planets"))
                .withQueryParam("search", equalTo("ErrorPlanet"))
                .willReturn(serverError()));

        assertThatThrownBy(() -> swapiService.consultSwAPI("ErrorPlanet"))
                .isInstanceOf(SWAPIException.class);
    }

    @Test
    void getQuantityOfApparitionInMovies_shouldReturnCorrectFilmCount() {
        PropertiesDto planet = new PropertiesDto();
        planet.setName("Naboo");
        planet.setFilms(List.of("film1", "film2", "film3", "film4"));

        SwapiDto swapiDto = new SwapiDto();
        swapiDto.setResults(List.of(planet));

        int count = swapiService.getQuantityOfApparitionInMovies("Naboo", swapiDto);

        assertThat(count).isEqualTo(4);
    }

    @Test
    void getQuantityOfApparitionInMovies_shouldBeCaseInsensitive() {
        PropertiesDto planet = new PropertiesDto();
        planet.setName("Coruscant");
        planet.setFilms(List.of("film1", "film2"));

        SwapiDto swapiDto = new SwapiDto();
        swapiDto.setResults(List.of(planet));

        assertThat(swapiService.getQuantityOfApparitionInMovies("CORUSCANT", swapiDto)).isEqualTo(2);
        assertThat(swapiService.getQuantityOfApparitionInMovies("coruscant", swapiDto)).isEqualTo(2);
    }

    @Test
    void getQuantityOfApparitionInMovies_shouldReturnZero_whenPlanetNotInResults() {
        PropertiesDto planet = new PropertiesDto();
        planet.setName("Alderaan");
        planet.setFilms(List.of("film1"));

        SwapiDto swapiDto = new SwapiDto();
        swapiDto.setResults(List.of(planet));

        int count = swapiService.getQuantityOfApparitionInMovies("Dagobah", swapiDto);

        assertThat(count).isZero();
    }

    @Test
    void getQuantityOfApparitionInMovies_shouldReturnZero_whenResultsIsNull() {
        SwapiDto swapiDto = new SwapiDto();
        swapiDto.setResults(null);

        int count = swapiService.getQuantityOfApparitionInMovies("Endor", swapiDto);

        assertThat(count).isZero();
    }
}
