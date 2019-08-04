package br.com.desafio.b2w.starWarsApi.services;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.desafio.b2w.starWarsApi.dto.PlanetaDTO;
import br.com.desafio.b2w.starWarsApi.dto.SwapiDTO;
import br.com.desafio.b2w.starWarsApi.exception.SWAPIException;
import br.com.desafio.b2w.starWarsApi.utils.MessageUtil;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@Service
public class SwapiService {

	private static final Logger logger = LogManager.getLogger(SwapiService.class);

	@Autowired
	private MessageUtil message;

	public SwapiDTO consumirSwapi(String nomePlaneta) {

		logger.info("Consumindo SWAPI.");

		try {

			String url = "https://swapi.co/api/planets?search=" + nomePlaneta;

			RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

			ResponseEntity<SwapiDTO> response = restTemplate.getForEntity(url, SwapiDTO.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new SWAPIException(this.message.getMessage("mensagem.erro.swapi"));
		}

		return null;
	}

	public Integer getQuantidadeDeAparicoesEmFilmes(String nomePlaneta, SwapiDTO retornoSwapi) {
		
		PlanetaDTO planetaDTO = retornoSwapi.getResults()
				.stream()
				.filter(value -> nomePlaneta.equalsIgnoreCase(value.getName()))
				.findFirst()
				.orElse(new PlanetaDTO());

		return Optional.ofNullable(planetaDTO.getFilms()).map(List::size).orElse(0);
	}
}
