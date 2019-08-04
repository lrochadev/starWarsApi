package br.com.desafio.b2w.starWarsApi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.desafio.b2w.starWarsApi.model.Planeta;

/**
 * 
 * @author Leonardo Rocha
 *
 */
public  interface PlanetaRepository extends MongoRepository<Planeta, String>{
	Optional<List<Planeta>> findByNome(String nome);
}