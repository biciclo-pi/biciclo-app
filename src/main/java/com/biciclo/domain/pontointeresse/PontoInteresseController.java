package com.biciclo.domain.pontointeresse;

import com.biciclo.domain.pontointeresse.dto.PoiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pois")
public class PontoInteresseController {

    private final PontoInteresseService service;

    public PontoInteresseController(PontoInteresseService service) {
        this.service = service;
    }

    @GetMapping
    public List<PoiResponse> listar(@RequestParam double latitude,
                                    @RequestParam double longitude,
                                    @RequestParam(defaultValue = "1000") double raio) {
        return service.buscarProximos(latitude, longitude, raio);
    }
}
