package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.swapi.PropertiesDto;
import br.com.challenge.b2w.starWarsApi.dto.swapi.SwapiDto;
import br.com.challenge.b2w.starWarsApi.exception.SWAPIException;
import br.com.challenge.b2w.starWarsApi.utils.MessageUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author Leonardo Rocha
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwapiService {

    @Value("${starwars.api.url}")
    private String starWarsApiUrl;

    private final MessageUtil message;
    private final RestClient restClient;
    private final CircuitBreaker swapiCircuitBreaker;

    @Cacheable(value = "swapi-planets", key = "#planetName.toLowerCase()")
    public SwapiDto consultSwAPI(final String planetName) {
        return swapiCircuitBreaker.executeSupplier(() -> fetchFromSwapi(planetName));
    }

    private SwapiDto fetchFromSwapi(final String planetName) {
        log.info("Finding planet : {}, in SWAPI.", planetName);
        try {
            final SwapiDto response = restClient.get()
                    .uri(starWarsApiUrl + planetName)
                    .retrieve()
                    .body(SwapiDto.class);
            log.info("Found: {} planet!", planetName);
            return response;
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw new SWAPIException(this.message.getMessage("error.message.when.consult.swapi"));
        }
    }

    public Integer getQuantityOfApparitionInMovies(final String planetName, final SwapiDto returnOfSwapi) {
        return ofNullable(returnOfSwapi.getResults()).orElse(new ArrayList<>())
                .stream()
                .filter(propertiesDto -> planetName.equalsIgnoreCase(propertiesDto.getName()))
                .findFirst()
                .map(PropertiesDto::getFilms)
                .map(List::size)
                .orElse(0);
    }
}
