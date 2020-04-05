package br.com.desafio.b2w.starWarsApi.resources;

import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.services.PlanetaService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@RestController
@RequestMapping("/api/planetas")
@PropertySource("classpath:messages.properties")
public class PlanetaResource {

	private final PlanetaService planetaService;

	@Autowired
	public PlanetaResource(PlanetaService planetaService) {
		this.planetaService = planetaService;
	}

	/*
	 * Retorna a lista com todos os planetas cadastrados.
	 */
	@GetMapping
	@ApiOperation(value = "${msg.info.swagger.endpoint.planeta.list}", response = Planeta[].class)
	public ResponseEntity<List<Planeta>> list() {
		return new ResponseEntity<>(this.planetaService.findAll(), HttpStatus.OK);
	}

	/*
	 * Cria um novo planeta.
	 */
	@PostMapping
	@ApiOperation(value = "${msg.info.swagger.endpoint.planeta.create}", response = Planeta[].class)
	public ResponseEntity<Planeta> create(@Validated @RequestBody Planeta planeta, HttpServletResponse response) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.planetaService.save(planeta));
	}

	/*
	 * Remove um planeta por id. 
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiOperation(value = "${msg.info.swagger.endpoint.planeta.delete}", response = Planeta[].class)
	public void delete(@PathVariable String id) {
		this.planetaService.delete(id);
	}

	/*
	 * Busca um planeta por id.
	 */
	@GetMapping("/{id}")
	@ApiOperation(value = "${msg.info.swagger.endpoint.planeta.find.byid}", response = Planeta[].class)
	public ResponseEntity<Planeta> findById(@PathVariable String id) {
		return new ResponseEntity<>(planetaService.findById(id), HttpStatus.OK);
	}

	/*
	 * Busca por planetas por nome (ignore case containing).
	 */
	@GetMapping("/name/{name}")
	@ApiOperation(value = "${msg.info.swagger.endpoint.planeta.find.byname}", response = Planeta[].class)
	public ResponseEntity<Optional<List<Planeta>>> findByNome(@PathVariable String name) {
		return new ResponseEntity<>(planetaService.findByNome(name), HttpStatus.OK);
	}
}
