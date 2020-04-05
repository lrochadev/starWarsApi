package br.com.desafio.b2w.starWarsApi.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

    @NotEmpty(message = "Nome é obrigatório")
    private String nome;
    
    @NotEmpty(message = "Clima é obrigatório")
    private String clima;

    @NotEmpty(message = "Terreno é obrigatório")
    private String terreno;

    private int qtdAparicoesEmFilmes = 0;
}