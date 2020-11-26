package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.PlanetDTO;
import br.com.challenge.b2w.starWarsApi.dto.SwapiDTO;
import br.com.challenge.b2w.starWarsApi.exception.SWAPIException;
import br.com.challenge.b2w.starWarsApi.utils.MessageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author Leonardo Rocha
 */
@Service
public class SwapiService {

    private static final Logger logger = LogManager.getLogger(SwapiService.class);
    private final MessageUtil message;
    @Value("${starwars.api.url}")
    private String starWarsApiUrl;

    @Autowired
    public SwapiService(MessageUtil message) {
        this.message = message;
    }

    public SwapiDTO consultSwAPI(String planetName) {

        logger.info("Finding planet : {}, in SWAPI.", planetName);

        try {

            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

            ResponseEntity<SwapiDTO> response = restTemplate.getForEntity(this.starWarsApiUrl + planetName, SwapiDTO.class);

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

    public Integer getQuantityOfApparitionInMovies(String planetName, SwapiDTO returnOfSwapi) {

        PlanetDTO planetDTO = ofNullable(returnOfSwapi.getResults()).orElseGet(Collections::emptyList)
                .stream()
                .filter(value -> planetName.equalsIgnoreCase(value.getName()))
                .findFirst()
                .orElse(new PlanetDTO());

        return ofNullable(planetDTO.getFilms()).map(List::size).orElse(0);
    }
}
