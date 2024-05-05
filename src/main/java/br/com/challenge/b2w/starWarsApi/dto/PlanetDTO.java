package br.com.challenge.b2w.starWarsApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Leonardo Rocha
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3836805433196896673L;
    private String name;
    private String url;
    private String rotation_period;
    private String orbital_period;
    private String diameter;
    private String climate;
    private String gravity;
    private String terrain;
    private String surface_water;
    private String population;
    private String created;
    private String edited;
    private List<String> films = new LinkedList<>();
    private List<String> residents;
}
