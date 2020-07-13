package br.com.desafio.b2w.starWarsApi.services;

import br.com.desafio.b2w.starWarsApi.dto.SwapiDTO;
import br.com.desafio.b2w.starWarsApi.exception.PlanetaInexistenteException;
import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.repository.PlanetaRepository;
import br.com.desafio.b2w.starWarsApi.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * @author Leonardo Rocha
 */
@Service
public class PlanetaServiceImpl implements PlanetaService {

    private final MessageUtil message;
    private final SwapiService swapiService;
    private final PlanetaRepository planetaRepository;

    @Autowired
    public PlanetaServiceImpl(MessageUtil message, SwapiService swapiService, PlanetaRepository planetaRepository) {
        this.message = message;
        this.swapiService = swapiService;
        this.planetaRepository = planetaRepository;
    }

    @Override
    public Planeta save(Planeta planeta) {

        SwapiDTO responseSwapi = this.swapiService.consumirSwapi(planeta.getNome());

        if (nonNull(responseSwapi)) {
            planeta.setQtdAparicoesEmFilmes(this.swapiService.getQuantidadeDeAparicoesEmFilmes(planeta.getNome(), responseSwapi));
        }

        return planetaRepository.save(planeta);
    }

    @Override
    public void delete(String id) {
        if (nonNull(findById(id))) {
            this.planetaRepository.deleteById(id);
        }
    }

    @Override
    public Optional<List<Planeta>> findByNome(String nome) {
        return planetaRepository.findByNomeIgnoreCaseContaining(nome);
    }

    @Override
    public Planeta findById(String id) {
        return planetaRepository.findById(id).orElseThrow(
                () -> new PlanetaInexistenteException(message.getMessage("mensagem.erro.planeta.inexistente")));
    }

    @Override
    public List<Planeta> findAll() {
        return planetaRepository.findAll();
    }
}
