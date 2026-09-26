package com.biciclo.domain.cupom;

import com.biciclo.common.enums.StatusCupom;
import com.biciclo.domain.recompensa.Recompensa;
import com.biciclo.domain.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "cupons")
@Getter
@Setter
@NoArgsConstructor
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_validacao", nullable = false, unique = true, length = 64)
    private String codigoValidacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recompensa_id", nullable = false)
    private Recompensa recompensa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ciclista_id", nullable = false)
    private Usuario ciclista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCupom status = StatusCupom.PENDENTE;

    @Column(name = "data_resgate", nullable = false, updatable = false)
    private Instant dataResgate;

    @Column(name = "data_utilizacao")
    private Instant dataUtilizacao;

    @PrePersist
    void onCreate() {
        if (dataResgate == null) {
            dataResgate = Instant.now();
        }
        if (status == null) {
            status = StatusCupom.PENDENTE;
        }
    }
}
