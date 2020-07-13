package br.com.desafio.b2w.starWarsApi.repository;

import br.com.desafio.b2w.starWarsApi.model.Planeta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Leonardo Rocha
 */
@Repository
public interface PlanetaRepository extends MongoRepository<Planeta, String> {
    Optional<List<Planeta>> findByNomeIgnoreCaseContaining(String nome);
}