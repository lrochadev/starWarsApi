package br.com.challenge.b2w.starWarsApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Leonardo Rocha
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -864757268381721896L;

    private String id;

    @NotEmpty(message = "Name is mandatory")
    private String name;

    @NotEmpty(message = "Climate is mandatory")
    private String climate;

    @NotEmpty(message = "Terrain is mandatory")
    private String terrain;

    @Hidden
    private int quantityOfApparitionInMovies = 0;
}