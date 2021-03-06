package br.com.challenge.b2w.starWarsApi.resources;

import br.com.challenge.b2w.starWarsApi.model.Planet;
import br.com.challenge.b2w.starWarsApi.services.PlanetService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @ApiOperation(value = "${message.info.swagger.endpoint.planet.list}", response = Planet[].class)
    public ResponseEntity<List<Planet>> list() {
        return new ResponseEntity<>(this.planetService.findAll(), OK);
    }

    @PostMapping
    @ApiOperation(value = "${message.info.swagger.endpoint.planet.create}", response = Planet[].class)
    public ResponseEntity<Planet> create(@Validated @RequestBody Planet planet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.planetService.save(planet));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @ApiOperation(value = "${message.info.swagger.endpoint.planet.delete}", response = Planet[].class)
    public void delete(@PathVariable String id) {
        this.planetService.delete(id);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "${message.info.swagger.endpoint.planet.find.byid}", response = Planet[].class)
    public ResponseEntity<Planet> findById(@PathVariable String id) {
        return new ResponseEntity<>(planetService.findById(id), OK);
    }

    @GetMapping("/name/{name}")
    @ApiOperation(value = "${message.info.swagger.endpoint.planet.find.byname}", response = Planet[].class)
    public ResponseEntity<Optional<List<Planet>>> findByNome(@PathVariable String name) {
        return new ResponseEntity<>(planetService.findByName(name), OK);
    }
}
