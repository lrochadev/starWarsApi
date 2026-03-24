package br.com.challenge.b2w.starWarsApi.mappers;

import br.com.challenge.b2w.starWarsApi.dto.PlanetDto;
import br.com.challenge.b2w.starWarsApi.model.Planet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanetMapper {

    Planet toDomain(PlanetDto dto);

    PlanetDto toDto(Planet entity);

    List<PlanetDto> mapToListDto(List<Planet> planets);
}
