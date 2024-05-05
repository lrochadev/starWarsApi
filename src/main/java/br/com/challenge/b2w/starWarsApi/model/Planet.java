package br.com.challenge.b2w.starWarsApi.model;


import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;


/**
 * @author Leonardo Rocha
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "planet")
public class Planet implements Serializable {

    @Serial
    private static final long serialVersionUID = 7229395964895089019L;

    @Id
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