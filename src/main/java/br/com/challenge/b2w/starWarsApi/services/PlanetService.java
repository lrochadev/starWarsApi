package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.PlanetDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @author Leonardo Rocha
 */
public interface PlanetService {

    Page<PlanetDto> findAll(Pageable pageable);

    PlanetDto findById(String id);

    PlanetDto save(PlanetDto planet);

    void delete(String id);

    Optional<List<PlanetDto>> findByName(String name);
}
