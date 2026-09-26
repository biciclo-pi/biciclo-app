package com.biciclo.domain.trajeto;

import com.biciclo.common.enums.StatusAnalise;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.LineString;

import java.time.Instant;

@Entity
@Table(name = "trajetos")
@Getter
@Setter
@NoArgsConstructor
public class Trajeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ciclista_id", nullable = false)
    private Usuario ciclista;

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "geom", columnDefinition = "geometry(LineString, 4326)", nullable = false)
    private LineString geom;

    @Column(name = "distancia_metros")
    private Double distanciaMetros;

    @Column(name = "duracao_segundos")
    private Integer duracaoSegundos;

    @Column(name = "velocidade_media")
    private Double velocidadeMedia;

    @Column(name = "pontos_ganhos")
    private Integer pontosGanhos;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_analise", nullable = false, length = 20)
    private StatusAnalise statusAnalise = StatusAnalise.PENDENTE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (statusAnalise == null) {
            statusAnalise = StatusAnalise.PENDENTE;
        }
    }
}
