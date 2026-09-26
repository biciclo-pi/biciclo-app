package com.biciclo.domain.auth.dto;

import com.biciclo.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String senha;

    @NotNull
    private Role role;

    // Campos obrigatórios apenas quando role = PARTNER
    private String nomeFantasia;

    private String cnpj;

    private Double latitude;

    private Double longitude;
}
