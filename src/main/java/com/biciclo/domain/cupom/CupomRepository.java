package com.biciclo.domain.cupom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CupomRepository extends JpaRepository<Cupom, Long> {

    Optional<Cupom> findByCodigoValidacao(String codigoValidacao);

    List<Cupom> findByCiclistaIdOrderByDataResgateDesc(Long ciclistaId);
}
