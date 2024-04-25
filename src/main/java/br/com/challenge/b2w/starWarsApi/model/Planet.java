package br.com.challenge.b2w.starWarsApi.model;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @author Leonardo Rocha
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "planet")
public class Planet {

    @Id
    private String id;

    @NotNull
    @NotEmpty(message = "Name is mandatory")
    private String name;

    @NotEmpty(message = "Climate is mandatory")
    private String climate;

    @NotEmpty(message = "Terrain is mandatory")
    private String terrain;

    private int quantityOfApparitionInMovies = 0;
}