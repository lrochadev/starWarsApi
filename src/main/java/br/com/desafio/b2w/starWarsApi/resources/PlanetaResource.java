package br.com.desafio.b2w.starWarsApi.resources;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.desafio.b2w.starWarsApi.model.Planeta;
import br.com.desafio.b2w.starWarsApi.services.PlanetaService;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@RestController
@RequestMapping("/api/planetas")
public class PlanetaResource {

	@Autowired
	private PlanetaService planetaService;

	@GetMapping
	public ResponseEntity<List<Planeta>> list() {
		return new ResponseEntity<>(this.planetaService.findAll(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Planeta> create(@Valid @RequestBody Planeta planeta, HttpServletResponse response) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.planetaService.save(planeta));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String id) {
		this.planetaService.delete(id);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Planeta> findById(@PathVariable String id) {
		return new ResponseEntity<>(planetaService.findById(id), HttpStatus.OK);
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<Optional<List<Planeta>>> findByName(@PathVariable String name) {
		return new ResponseEntity<>(planetaService.findByNome(name), HttpStatus.OK);
	}
}
