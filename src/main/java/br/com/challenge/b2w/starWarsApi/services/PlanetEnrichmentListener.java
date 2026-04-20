package br.com.challenge.b2w.starWarsApi.services;

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

    private final PlanetEnrichmentUpdater updater;

    @Async
    @EventListener
    public void onPlanetCreated(final PlanetCreatedEvent event) {
        try {
            updater.enrich(event);
        } catch (CallNotPermittedException e) {
            log.warn("Async enrichment skipped for planet '{}' (circuit open); fallback to lazy enrichment on read",
                    event.name());
        }
    }
}
