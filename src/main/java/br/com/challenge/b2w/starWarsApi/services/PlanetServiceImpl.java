package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.PlanetDto;
import br.com.challenge.b2w.starWarsApi.dto.swapi.SwapiDto;
import br.com.challenge.b2w.starWarsApi.exception.PlanetNotFoundException;
import br.com.challenge.b2w.starWarsApi.exception.SWAPIException;
import br.com.challenge.b2w.starWarsApi.mappers.PlanetMapper;
import br.com.challenge.b2w.starWarsApi.model.Planet;
import br.com.challenge.b2w.starWarsApi.repository.PlanetRepository;
import br.com.challenge.b2w.starWarsApi.utils.MessageUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Leonardo Rocha
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanetServiceImpl implements PlanetService {

    private final PlanetMapper planetMapper;
    private final MessageUtil message;
    private final SwapiService swapiService;
    private final PlanetRepository planetRepository;

    @Override
    public PlanetDto save(final PlanetDto planetDto) {
        final Planet saved = planetRepository.save(planetMapper.toDomain(planetDto));
        return planetMapper.toDto(saved);
    }

    @Override
    public void delete(String id) {
        if (!planetRepository.existsById(id)) {
            throw new PlanetNotFoundException(message.getMessage("error.message.planet.notfound"));
        }
        planetRepository.deleteById(id);
    }

    @Override
    public Optional<List<PlanetDto>> findByName(final String name) {
        return planetRepository.findByNameIgnoreCaseContaining(name)
                .map(planets -> planets.stream().map(this::enrichAndSaveIfNeeded).toList());
    }

    @Override
    public PlanetDto findById(final String id) {
        final Planet planet = planetRepository.findById(id)
                .orElseThrow(() -> new PlanetNotFoundException(message.getMessage("error.message.planet.notfound")));
        return enrichAndSaveIfNeeded(planet);
    }

    @Override
    public Page<PlanetDto> findAll(final Pageable pageable) {
        return planetRepository.findAll(pageable).map(this::enrichAndSaveIfNeeded);
    }

    private PlanetDto enrichAndSaveIfNeeded(final Planet planet) {
        if (planet.getQuantityOfApparitionInMovies() == null) {
            try {
                final SwapiDto swapiDto = swapiService.consultSwAPI(planet.getName());
                planet.setQuantityOfApparitionInMovies(swapiService.getQuantityOfApparitionInMovies(planet.getName(), swapiDto));
                planetRepository.save(planet);
            } catch (CallNotPermittedException | SWAPIException e) {
                log.warn("SWAPI unavailable for planet '{}' ({}), returning count=0 as fallback",
                        planet.getName(), e.getClass().getSimpleName());
                planet.setQuantityOfApparitionInMovies(0);
            }
        }
        return planetMapper.toDto(planet);
    }

}
