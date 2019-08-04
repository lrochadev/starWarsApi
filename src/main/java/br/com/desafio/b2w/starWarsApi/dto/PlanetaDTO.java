package br.com.desafio.b2w.starWarsApi.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties()
public class PlanetaDTO implements Serializable {

	private static final long serialVersionUID = 1L;

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
	private List<String> films;
	private List<String> residents;
}
