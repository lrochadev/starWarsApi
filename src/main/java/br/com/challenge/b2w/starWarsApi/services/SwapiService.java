package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.swapi.PropertiesDto;
import br.com.challenge.b2w.starWarsApi.dto.swapi.SwapiDto;
import br.com.challenge.b2w.starWarsApi.exception.SWAPIException;
import br.com.challenge.b2w.starWarsApi.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author Leonardo Rocha
 */
@Service
@RequiredArgsConstructor
public class SwapiService {

    private static final Logger logger = LogManager.getLogger(SwapiService.class);

    @Value("${starwars.api.url}")
    private String starWarsApiUrl;

    private final MessageUtil message;

    private final RestTemplate restTemplate;

    public SwapiDto consultSwAPI(final String planetName) {

        logger.info("Finding planet : {}, in SWAPI.", planetName);

        try {

            final ResponseEntity<SwapiDto> response = restTemplate.getForEntity(this.starWarsApiUrl + planetName, SwapiDto.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Found: {} planet!", planetName);
                return response.getBody();
            }

        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            throw new SWAPIException(this.message.getMessage("error.message.when.consult.swapi"));
        }

        return null;
    }

    public Integer getQuantityOfApparitionInMovies(final String planetName, final SwapiDto returnOfSwapi) {
        return ofNullable(returnOfSwapi.getResults()).orElse(new ArrayList<>())
                .stream()
                .filter(propertiesDTO -> planetName.equalsIgnoreCase(propertiesDTO.getName()))
                .findFirst()
                .map(PropertiesDto::getFilms)
                .map(List::size)
                .orElse(0);
    }
}
