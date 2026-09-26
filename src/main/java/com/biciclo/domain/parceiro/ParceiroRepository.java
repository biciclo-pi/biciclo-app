package com.biciclo.domain.parceiro;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParceiroRepository extends JpaRepository<Parceiro, Long> {

    boolean existsByCnpj(String cnpj);
}
