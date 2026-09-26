package com.biciclo.domain.pontointeresse.dto;

import com.biciclo.common.enums.TipoPoi;
import com.biciclo.domain.pontointeresse.PontoInteresse;

public record PoiResponse(
        Long id,
        TipoPoi tipo,
        String descricao,
        Double latitude,
        Double longitude,
        Integer capacidade
) {

    public static PoiResponse from(PontoInteresse poi) {
        return new PoiResponse(
                poi.getId(),
                poi.getTipo(),
                poi.getDescricao(),
                poi.getGeom().getY(),
                poi.getGeom().getX(),
                poi.getCapacidade()
        );
    }
}
