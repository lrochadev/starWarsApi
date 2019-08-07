package br.com.desafio.b2w.starWarsApi.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.desafio.b2w.starWarsApi.dto.SwapiDTO;
import br.com.desafio.b2w.starWarsApi.exception.PlanetaInexistenteException;
import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.repository.PlanetaRepository;
import br.com.desafio.b2w.starWarsApi.utils.MessageUtil;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@Service
public class PlanetaServiceImpl implements PlanetaService {

	@Autowired
	private MessageUtil message;

	@Autowired
	private SwapiService swapiService;

	@Autowired
	private PlanetaRepository planetaRepository;

	/**
	 * Salva o planeta na base de dados.
	 */
	@Override
	public Planeta save(Planeta planeta) {

		String nomePlaneta = planeta.getNome();

		SwapiDTO responseSwapi = this.swapiService.consumirSwapi(nomePlaneta);

		if (responseSwapi != null) {
			Integer qtdAparicoesEmFilmes = this.swapiService.getQuantidadeDeAparicoesEmFilmes(nomePlaneta, responseSwapi);
			planeta.setQtdAparicoesEmFilmes(qtdAparicoesEmFilmes);
		}

		return planetaRepository.save(planeta);
	}

	/**
	 * Verifica se o planeta existe antes de deletar. Caso não exista, uma lança uma PlanetaInexistenteException.
	 */
	@Override
	public void delete(String id) {
		if (this.findById(id) != null) {
			this.planetaRepository.deleteById(id);
		}
	}

	@Override
	public Optional<List<Planeta>> findByNome(String nome) {
		return planetaRepository.findByNomeIgnoreCaseContaining(nome);
	}

	@Override
	public Planeta findById(String id) {
		return exists(planetaRepository.findById(id));
	}

	@Override
	public List<Planeta> findAll() {
		return planetaRepository.findAll();
	}

	private Planeta exists(Optional<Planeta> planeta) {
		return planeta.orElseThrow(
				() -> new PlanetaInexistenteException(message.getMessage("mensagem.erro.planeta.inexistente")));
	}
}
