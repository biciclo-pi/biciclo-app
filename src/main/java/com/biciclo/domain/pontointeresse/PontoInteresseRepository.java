package com.biciclo.domain.pontointeresse;

import com.biciclo.common.enums.TipoPoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PontoInteresseRepository extends JpaRepository<PontoInteresse, Long> {

    /**
     * Busca espacial por raio (em metros) usando PostGIS.
     * A conversão para ::geography permite medir distâncias em metros sobre o
     * elipsoide WGS84, atendendo ao RF05.
     *
     * @param lng  longitude do ponto de referência (X)
     * @param lat  latitude do ponto de referência (Y)
     * @param raio raio em metros
     */
    @Query(value = """
            SELECT * FROM pontos_interesse p
            WHERE p.ativo = TRUE
              AND ST_DWithin(
                    p.geom::geography,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                    :raio)
            ORDER BY ST_Distance(
                    p.geom::geography,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography)
            LIMIT :limite
            """, nativeQuery = true)
    List<PontoInteresse> buscarProximos(@Param("lng") double lng,
                                        @Param("lat") double lat,
                                        @Param("raio") double raio,
                                        @Param("limite") int limite);

    List<PontoInteresse> findByTipoAndAtivoTrue(TipoPoi tipo);
}
