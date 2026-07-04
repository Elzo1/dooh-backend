package com.dooh.signage.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.LoginRequest;
import com.dooh.signage.dto.LoginResponse;
import com.dooh.signage.dto.RefreshTokenRequest;
import com.dooh.signage.dto.UsuarioRequest;
import com.dooh.signage.model.Usuario;
import com.dooh.signage.repository.UsuarioRepository;
import com.dooh.signage.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (usuario == null || !usuario.getAtivo()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }

        List<String> roles = List.of(usuario.getRole());

        String token = jwtUtil.generateToken(usuario.getUsername(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getUsername(), roles);

        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(usuario.getUsername())
                .nome(usuario.getNome())
                .roles(roles)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            String username = jwtUtil.extractUsername(refreshToken);

            if (!jwtUtil.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido.");
            }

            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

            if (!usuario.getAtivo() || !jwtUtil.validateToken(refreshToken, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expirado.");
            }

            List<String> roles = List.of(usuario.getRole());

            return ResponseEntity.ok(LoginResponse.builder()
                    .token(jwtUtil.generateToken(usuario.getUsername(), roles))
                    .refreshToken(jwtUtil.generateRefreshToken(usuario.getUsername(), roles))
                    .username(usuario.getUsername())
                    .nome(usuario.getNome())
                    .roles(roles)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logout realizado.");
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> criarUsuario(@RequestBody UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username já cadastrado.");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .username(request.getUsername())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(request.getRole() != null ? request.getRole().toUpperCase() : "OPERADOR")
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuário criado com sucesso.");
    }
}