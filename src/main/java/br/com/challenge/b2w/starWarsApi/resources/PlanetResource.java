package br.com.challenge.b2w.starWarsApi.resources;

import br.com.challenge.b2w.starWarsApi.model.Planet;
import br.com.challenge.b2w.starWarsApi.services.PlanetService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Leonardo Rocha
 */
@RestController
@RequestMapping("/api/planets")
@PropertySource("classpath:messages.properties")
@RequiredArgsConstructor
public class PlanetResource {

    private final PlanetService planetService;

    @GetMapping
    @Operation(summary = "${message.info.swagger.endpoint.planet.list}")
    public ResponseEntity<List<Planet>> list() {
        return new ResponseEntity<>(this.planetService.findAll(), OK);
    }

    @PostMapping
    @Operation(summary = "${message.info.swagger.endpoint.planet.create}")
    public ResponseEntity<Planet> create(@Validated @RequestBody Planet planet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.planetService.save(planet));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "${message.info.swagger.endpoint.planet.delete}")
    public void delete(@PathVariable String id) {
        this.planetService.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "${message.info.swagger.endpoint.planet.find.byid}")
    public ResponseEntity<Planet> findById(@PathVariable String id) {
        return new ResponseEntity<>(planetService.findById(id), OK);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "${message.info.swagger.endpoint.planet.find.byname}")
    public ResponseEntity<List<Planet>> findByNome(@PathVariable String name) {
        return new ResponseEntity<>(planetService.findByName(name).orElse(emptyList()), OK);
    }
}
