package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.SwapiDTO;
import br.com.challenge.b2w.starWarsApi.exception.PlanetNotFoundException;
import br.com.challenge.b2w.starWarsApi.model.Planet;
import br.com.challenge.b2w.starWarsApi.repository.PlanetRepository;
import br.com.challenge.b2w.starWarsApi.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * @author Leonardo Rocha
 */
@Service
@RequiredArgsConstructor
public class PlanetServiceImpl implements PlanetService {

    private final MessageUtil message;
    private final SwapiService swapiService;
    private final PlanetRepository planetRepository;

    @Override
    public Planet save(final Planet planet) {

        final SwapiDTO responseSwapi = this.swapiService.consultSwAPI(planet.getName());

        if (nonNull(responseSwapi)) {
            responseSwapi.setResult(null);
            planet.setQuantityOfApparitionInMovies(this.swapiService.getQuantityOfApparitionInMovies(planet.getName(), responseSwapi));
        }

        return planetRepository.save(planet);
    }

    @Override
    public void delete(String id) {
        if (nonNull(findById(id))) {
            this.planetRepository.deleteById(id);
        }
    }

    @Override
    public Optional<List<Planet>> findByName(final String nome) {
        return planetRepository.findByNameIgnoreCaseContaining(nome);
    }

    @Override
    public Planet findById(final String id) {
        return planetRepository.findById(id)
                .orElseThrow(() -> new PlanetNotFoundException(message.getMessage("error.message.planet.notfound")));
    }

    @Override
    public List<Planet> findAll() {
        return planetRepository.findAll();
    }

}
