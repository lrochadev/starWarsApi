package br.com.desafio.b2w.starWarsApi.services;

import br.com.desafio.b2w.starWarsApi.dto.PlanetaDTO;
import br.com.desafio.b2w.starWarsApi.dto.SwapiDTO;
import br.com.desafio.b2w.starWarsApi.exception.SWAPIException;
import br.com.desafio.b2w.starWarsApi.utils.MessageUtil;
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

    /**
     * Método responsável por buscar informalções do Planeta em uma API externa (SWAPI).
     *
     * @param nomePlaneta
     * @return SwapiDTO
     */
    public SwapiDTO consumirSwapi(String nomePlaneta) {

        logger.info("Buscando planeta: {}, na SWAPI.", nomePlaneta);

        try {

            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

            ResponseEntity<SwapiDTO> response = restTemplate.getForEntity(this.starWarsApiUrl + nomePlaneta, SwapiDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Planeta: {} encontrado!", nomePlaneta);
                return response.getBody();
            }

        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            throw new SWAPIException(this.message.getMessage("mensagem.erro.swapi"));
        }

        return null;
    }

    /**
     * Recupera a quantidade de aparições em filmes.
     *
     * @param nomePlaneta
     * @param retornoSwapi
     * @return Integer
     */
    public Integer getQuantidadeDeAparicoesEmFilmes(String nomePlaneta, SwapiDTO retornoSwapi) {

        PlanetaDTO planetaDTO = ofNullable(retornoSwapi.getResults()).orElseGet(Collections::emptyList)
                .stream()
                .filter(value -> nomePlaneta.equalsIgnoreCase(value.getName()))
                .findFirst()
                .orElse(new PlanetaDTO());

        return ofNullable(planetaDTO.getFilms()).map(List::size).orElse(0);
    }
}
