package br.com.challenge.b2w.starWarsApi.mappers;

import br.com.challenge.b2w.starWarsApi.dto.PlanetDto;
import br.com.challenge.b2w.starWarsApi.model.Planet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlanetMapper {

    PlanetMapper INSTANCE = Mappers.getMapper(PlanetMapper.class);

    Planet toDomain(PlanetDto dto);

    PlanetDto toDto(Planet entity);

    List<PlanetDto> mapToListDto(List<Planet> planets);
}
