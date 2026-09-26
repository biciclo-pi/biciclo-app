package com.biciclo.domain.auth;

import com.biciclo.common.enums.Role;
import com.biciclo.common.exception.BusinessException;
import com.biciclo.domain.auth.dto.LoginRequest;
import com.biciclo.domain.auth.dto.LoginResponse;
import com.biciclo.domain.auth.dto.RegisterRequest;
import com.biciclo.domain.parceiro.Parceiro;
import com.biciclo.domain.parceiro.ParceiroRepository;
import com.biciclo.domain.usuario.Usuario;
import com.biciclo.domain.usuario.UsuarioRepository;
import com.biciclo.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ParceiroRepository parceiroRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       ParceiroRepository parceiroRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.parceiroRepository = parceiroRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(request.getRole());
        usuarioRepository.save(usuario);

        if (request.getRole() == Role.PARTNER) {
            Parceiro parceiro = new Parceiro();
            parceiro.setUsuario(usuario);
            parceiro.setNomeFantasia(requirePartnerField(request.getNomeFantasia(), "nomeFantasia"));
            parceiro.setCnpj(requirePartnerField(request.getCnpj(), "cnpj"));
            parceiro.setLatitude(request.getLatitude());
            parceiro.setLongitude(request.getLongitude());
            parceiroRepository.save(parceiro);
        }

        String token = jwtService.generateToken(usuario.getId(), usuario.getEmail(), usuario.getRole());
        return new LoginResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        String token = jwtService.generateToken(usuario.getId(), usuario.getEmail(), usuario.getRole());
        return new LoginResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
    }

    private String requirePartnerField(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Campo '" + field + "' é obrigatório para cadastro de parceiro");
        }
        return value;
    }
}
