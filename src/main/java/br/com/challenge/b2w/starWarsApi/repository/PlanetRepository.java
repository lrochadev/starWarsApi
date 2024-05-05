package br.com.challenge.b2w.starWarsApi.repository;

import br.com.challenge.b2w.starWarsApi.model.Planet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Leonardo Rocha
 */
@Repository
public interface PlanetRepository extends MongoRepository<Planet, String> {
    Optional<List<Planet>> findByNameIgnoreCaseContaining(String name);
}