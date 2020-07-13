package br.com.desafio.b2w.starWarsApi.dto;

/**
 * @author Leonardo Rocha
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class SwapiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PlanetaDTO> results;
}
