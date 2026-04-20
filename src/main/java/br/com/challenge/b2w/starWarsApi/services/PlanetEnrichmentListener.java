package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.swapi.SwapiDto;
import br.com.challenge.b2w.starWarsApi.exception.SWAPIException;
import br.com.challenge.b2w.starWarsApi.model.Planet;
import br.com.challenge.b2w.starWarsApi.repository.PlanetRepository;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanetEnrichmentListener {

    private final SwapiService swapiService;
    private final PlanetRepository planetRepository;

    @Async
    @EventListener
    public void onPlanetCreated(final PlanetCreatedEvent event) {
        try {
            final SwapiDto swapiDto = swapiService.consultSwAPI(event.name());
            final Integer count = swapiService.getQuantityOfApparitionInMovies(event.name(), swapiDto);
            planetRepository.findById(event.id()).ifPresent(planet -> {
                planet.setQuantityOfApparitionInMovies(count);
                planetRepository.save(planet);
            });
        } catch (CallNotPermittedException | SWAPIException e) {
            log.warn("Async enrichment failed for planet '{}' ({}); will fall back to lazy enrichment on read",
                    event.name(), e.getClass().getSimpleName());
        }
    }
}
