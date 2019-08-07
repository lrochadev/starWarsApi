package br.com.desafio.b2w.starWarsApi.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "planeta")
public class Planeta {

    @Id
    private String id;

    @NotNull
    @NotEmpty(message = "Nomé é obrigatório")
    private String nome;
    
    @NotNull
    @NotEmpty(message = "Clima é obrigatório")
    private String clima;

    @NotNull
    @NotEmpty(message = "Terreno é obrigatório")
    private String terreno;

    private Integer qtdAparicoesEmFilmes = 0;
}