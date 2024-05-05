package br.com.challenge.b2w.starWarsApi.dto;

/**
 * @author Leonardo Rocha
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertiesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3390391804728401959L;
    private PlanetDTO properties;
    private String description;
}
