package com.biciclo.domain.extrato;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExtratoPontosRepository extends JpaRepository<ExtratoPontos, Long> {

    List<ExtratoPontos> findByCiclistaIdOrderByCreatedAtDesc(Long ciclistaId);
}
