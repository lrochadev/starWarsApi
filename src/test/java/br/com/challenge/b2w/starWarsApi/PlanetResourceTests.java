package br.com.challenge.b2w.starWarsApi;

import br.com.challenge.b2w.starWarsApi.exception.PlanetNotFoundException;
import br.com.challenge.b2w.starWarsApi.model.Planet;
import br.com.challenge.b2w.starWarsApi.resources.PlanetResource;
import br.com.challenge.b2w.starWarsApi.services.PlanetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Leonardo Rocha
 */
@ExtendWith(MockitoExtension.class)
public class PlanetResourceTests extends ControllerTest {

    private static final String RESOURCE_PLANET_PATH = "/api/planets/";
    private static final String RESOURCE_PLANET_PATH_WITH_ID = RESOURCE_PLANET_PATH + "/{id}";

    @Mock
    private PlanetService planetService;

    protected PlanetResourceTests() {
        super();
    }

    @BeforeEach
    public void setUp() {
        configure(new PlanetResource(planetService));
    }

    @Test
    public void listPlanetsShouldReturnStatusCode200() {
        doMvc(mockMvc -> {
            try {

                List<Planet> planets = List.of(new Planet("66297ba166cb2e6e5ca020b7", "Endor", "tropical", "mountains", 0));
                when(planetService.findAll()).thenReturn(planets);

                mockMvc.perform(get(RESOURCE_PLANET_PATH)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].id", is("66297ba166cb2e6e5ca020b7")))
                        .andExpect(jsonPath("$[0].name", is("Endor")))
                        .andExpect(jsonPath("$[0].climate", is("tropical")))
                        .andExpect(jsonPath("$[0].terrain", is("mountains")))
                        .andExpect(jsonPath("$[0].quantityOfApparitionInMovies", is(0)));

                verify(planetService, times(1)).findAll();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void getPlanetByIdWhenIdAreCorrectShouldReturnStatusCode200() {
        doMvc(mockMvc -> {
            try {

                final String id = "66297ba166cb2e6e5ca020b7";
                final Planet planet = new Planet(id, "Endor", "tropical", "mountains", 0);
                when(planetService.findById(anyString())).thenReturn(planet);

                mockMvc.perform(get(RESOURCE_PLANET_PATH_WITH_ID, id)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id", is(id)))
                        .andExpect(jsonPath("$.name", is("Endor")))
                        .andExpect(jsonPath("$.climate", is("tropical")))
                        .andExpect(jsonPath("$.terrain", is("mountains")))
                        .andExpect(jsonPath("$.quantityOfApparitionInMovies", is(0)));

                verify(planetService, times(1)).findById(id);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void getPlanetByIdWhenPlanetDoesNotExistShouldReturnStatusCode404() {
        doMvc(mockMvc -> {
            try {

                final String id = "-1";
                when(planetService.findById(any())).thenThrow(new PlanetNotFoundException("Planet not found."));

                mockMvc.perform(get(RESOURCE_PLANET_PATH_WITH_ID, id)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                        .andExpect(jsonPath("$.detail", is("Planet not found.")))
                        .andExpect(jsonPath("$.developerMessage", is("br.com.challenge.b2w.starWarsApi.exception.PlanetNotFoundException")))
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

                verify(planetService, times(1)).findById(id);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void deleteWhenIdExistsShouldReturnStatusCode200() {
        doMvc(mockMvc -> {
            try {

                final String id = "66297ba166cb2e6e5ca020b7";
                doNothing().when(planetService).delete(anyString());
                lenient().when(planetService.findById((id))).thenReturn(any(Planet.class));

                mockMvc.perform(delete(RESOURCE_PLANET_PATH_WITH_ID, id).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

                verify(planetService, times(1)).delete(id);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void createWhenNameIsNullShouldReturnStatusCode400BadRequest() {
        doMvc(mockMvc -> {
            try {

                final Planet planet = new Planet("66297ba166cb2e6e5ca020b7", null, "tropical", "mountains", 0);

                mockMvc.perform(post(RESOURCE_PLANET_PATH)
                                .content(jsonMapper.writeValueAsString(planet))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                        .andExpect(jsonPath("$.detail", is("Parameters with invalid format were sent.")))
                        .andExpect(jsonPath("$.developerMessage", is("org.springframework.web.bind.MethodArgumentNotValidException")))
                        .andExpect(jsonPath("$.title", is("Ocorreu um erro!")))
                        .andExpect(jsonPath("$.field", is("name")))
                        .andExpect(jsonPath("$.fieldMessage", is("Name is mandatory")));
                        // TODO: Falta validar o timestamp pelo pattern de data. Exemplo: 03/05/2024 16:35:45

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void createShouldPersistDataAndReturnStatusCode201() {
        doMvc(mockMvc -> {
            try {

                final String id = "66297ba166cb2e6e5ca020b7";
                final Planet planet = new Planet(id, "Naboo", "tropical", "mountains", 0);

                when(planetService.save(planet)).thenReturn(planet);

                mockMvc.perform(post(RESOURCE_PLANET_PATH)
                                .content(jsonMapper.writeValueAsString(planet))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id", is(id)))
                        .andExpect(jsonPath("$.name", is("Naboo")))
                        .andExpect(jsonPath("$.climate", is("tropical")))
                        .andExpect(jsonPath("$.terrain", is("mountains")))
                        .andExpect(jsonPath("$.quantityOfApparitionInMovies", is(0)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
