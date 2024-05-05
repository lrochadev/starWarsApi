package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.model.Planet;

import java.util.List;
import java.util.Optional;

/**
 * @author Leonardo Rocha
 */
public interface PlanetService {

    List<Planet> findAll();

    Planet findById(String id);

    Planet save(Planet planet);

    void delete(String id);

    Optional<List<Planet>> findByName(String name);
}
