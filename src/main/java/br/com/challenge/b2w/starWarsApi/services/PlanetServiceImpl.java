package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.PlanetDto;
import br.com.challenge.b2w.starWarsApi.dto.swapi.SwapiDto;
import br.com.challenge.b2w.starWarsApi.exception.PlanetNotFoundException;
import br.com.challenge.b2w.starWarsApi.mappers.PlanetMapper;
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

    private final PlanetMapper planetMapper;
    private final MessageUtil message;
    private final SwapiService swapiService;
    private final PlanetRepository planetRepository;

    @Override
    public PlanetDto save(final PlanetDto planetDto) {

        final SwapiDto responseSwapi = this.swapiService.consultSwAPI(planetDto.getName());

        if (nonNull(responseSwapi)) {
            planetDto.setQuantityOfApparitionInMovies(this.swapiService.getQuantityOfApparitionInMovies(planetDto.getName(), responseSwapi));
        }

        final Planet saved = planetRepository.save(planetMapper.toDomain(planetDto));

        return planetMapper.toDto(saved);
    }

    @Override
    public void delete(String id) {
        if (nonNull(findById(id))) {
            this.planetRepository.deleteById(id);
        }
    }

    @Override
    public Optional<List<PlanetDto>> findByName(final String name) {
        return planetRepository.findByNameIgnoreCaseContaining(name).map(planetMapper::mapToListDto);
    }

    @Override
    public PlanetDto findById(final String id) {
        final Planet planet = planetRepository.findById(id).orElseThrow(() -> new PlanetNotFoundException(message.getMessage("error.message.planet.notfound")));
        return planetMapper.toDto(planet);
    }

    @Override
    public List<PlanetDto> findAll() {
        return planetMapper.mapToListDto(planetRepository.findAll());
    }

}
