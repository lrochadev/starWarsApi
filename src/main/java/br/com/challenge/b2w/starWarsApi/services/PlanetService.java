package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.PlanetDto;

import java.util.List;
import java.util.Optional;

/**
 * @author Leonardo Rocha
 */
public interface PlanetService {

    List<PlanetDto> findAll();

    PlanetDto findById(String id);

    PlanetDto save(PlanetDto planet);

    void delete(String id);

    Optional<List<PlanetDto>> findByName(String name);
}
