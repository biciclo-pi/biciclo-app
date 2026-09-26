package com.biciclo.domain.pontointeresse;

import com.biciclo.common.exception.BusinessException;
import com.biciclo.domain.pontointeresse.dto.PoiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PontoInteresseService {

    private static final int LIMITE_RESULTADOS = 50;
    private static final double RAIO_MAXIMO_METROS = 5000;

    private final PontoInteresseRepository repository;

    public PontoInteresseService(PontoInteresseRepository repository) {
        this.repository = repository;
    }

    /**
     * Busca POIs próximos de uma coordenada, dentro de um raio em metros.
     * Limita o raio a 5 km (RNF02).
     */
    public List<PoiResponse> buscarProximos(double latitude, double longitude, double raio) {
        if (raio <= 0 || raio > RAIO_MAXIMO_METROS) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Raio deve ser maior que 0 e no máximo " + (int) RAIO_MAXIMO_METROS + " metros");
        }

        return repository.buscarProximos(longitude, latitude, raio, LIMITE_RESULTADOS)
                .stream()
                .map(PoiResponse::from)
                .toList();
    }
}
