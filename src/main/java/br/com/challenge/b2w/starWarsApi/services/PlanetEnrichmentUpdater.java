package br.com.challenge.b2w.starWarsApi.services;

import br.com.challenge.b2w.starWarsApi.dto.swapi.SwapiDto;
import br.com.challenge.b2w.starWarsApi.exception.SWAPIException;
import br.com.challenge.b2w.starWarsApi.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanetEnrichmentUpdater {

    private final SwapiService swapiService;
    private final PlanetRepository planetRepository;

    @Retryable(
            retryFor = SWAPIException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2.0))
    public void enrich(final PlanetCreatedEvent event) {
        final SwapiDto swapiDto = swapiService.consultSwAPI(event.name());
        final Integer count = swapiService.getQuantityOfApparitionInMovies(event.name(), swapiDto);
        planetRepository.findById(event.id()).ifPresent(planet -> {
            planet.setQuantityOfApparitionInMovies(count);
            planetRepository.save(planet);
        });
    }

    @Recover
    public void recover(final SWAPIException ex, final PlanetCreatedEvent event) {
        // exhausted retries — fallback lazy enrichment will run on next read
    }
}
