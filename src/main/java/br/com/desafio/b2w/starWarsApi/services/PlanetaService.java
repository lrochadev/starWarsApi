package br.com.desafio.b2w.starWarsApi.services;

import java.util.List;
import java.util.Optional;

import br.com.desafio.b2w.starWarsApi.model.Planeta;

/**
 * 
 * @author Leonardo Rocha
 *
 */
public interface PlanetaService {

	List<Planeta> findAll() ;

	Planeta findById(String id) ;
    
	Planeta save(Planeta planeta) ;
    
	void delete(String id);
    
	Optional<List<Planeta>> findByNome(String nome);
}
