package com.dooh.signage.service;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dooh.signage.dto.DispositivoAtivacaoResponse;
import com.dooh.signage.dto.DispositivoPingRequest;
import com.dooh.signage.dto.SolicitarPinRequest;
import com.dooh.signage.dto.VincularDispositivoRequest;
import com.dooh.signage.model.Dispositivo;
import com.dooh.signage.model.Tela;
import com.dooh.signage.repository.DispositivoRepository;
import com.dooh.signage.repository.TelaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final TelaRepository telaRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public DispositivoAtivacaoResponse solicitarPin(SolicitarPinRequest request, String ip, String userAgent) {
        String uuidRecebido = request != null ? request.getDeviceUuid() : null;

        if (uuidRecebido == null || uuidRecebido.isBlank()) {
            uuidRecebido = UUID.randomUUID().toString();
        }

        final String deviceUuidFinal = uuidRecebido.trim();

        Dispositivo dispositivo = dispositivoRepository.findByDeviceUuid(deviceUuidFinal)
                .orElseGet(() -> Dispositivo.builder()
                        .deviceUuid(deviceUuidFinal)
                        .build());

        String versaoPlayer = request != null ? request.getVersaoPlayer() : null;

        if (Boolean.TRUE.equals(dispositivo.getAtivado()) && dispositivo.getTela() != null) {
            dispositivo.setUltimaConexao(ZonedDateTime.now());
            dispositivo.setIp(ip);
            dispositivo.setUserAgent(userAgent);
            dispositivo.setVersaoPlayer(versaoPlayer);
            return toResponse(dispositivoRepository.save(dispositivo));
        }

        dispositivo.setPin(gerarPinUnico());
        dispositivo.setStatus("AGUARDANDO");
        dispositivo.setAtivado(false);
        dispositivo.setExpiraEm(ZonedDateTime.now().plusMinutes(10));
        dispositivo.setUltimaConexao(ZonedDateTime.now());
        dispositivo.setIp(ip);
        dispositivo.setUserAgent(userAgent);
        dispositivo.setVersaoPlayer(versaoPlayer);

        return toResponse(dispositivoRepository.save(dispositivo));
    }

    public DispositivoAtivacaoResponse buscarPorPin(String pin) {
        Dispositivo dispositivo = dispositivoRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("PIN não encontrado."));

        return toResponse(dispositivo);
    }

    public DispositivoAtivacaoResponse buscarPorDeviceUuid(String deviceUuid) {
        Dispositivo dispositivo = dispositivoRepository.findByDeviceUuid(deviceUuid)
                .orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado."));

        return toResponse(dispositivo);
    }

    @Transactional
    public DispositivoAtivacaoResponse vincular(VincularDispositivoRequest request) {
        if (request.getPin() == null || request.getPin().isBlank()) {
            throw new IllegalArgumentException("PIN é obrigatório.");
        }

        if (request.getTelaId() == null) {
            throw new IllegalArgumentException("Tela é obrigatória.");
        }

        Dispositivo dispositivo = dispositivoRepository.findByPin(request.getPin())
                .orElseThrow(() -> new IllegalArgumentException("PIN inválido."));

        if (dispositivo.getExpiraEm() != null && dispositivo.getExpiraEm().isBefore(ZonedDateTime.now())) {
            throw new IllegalArgumentException("PIN expirado. Gere outro PIN na TV.");
        }

        Tela tela = telaRepository.findById(request.getTelaId())
                .orElseThrow(() -> new IllegalArgumentException("Tela não encontrada."));

        dispositivo.setTela(tela);
        dispositivo.setAtivado(true);
        dispositivo.setStatus("ATIVADO");
        dispositivo.setPin(null);
        dispositivo.setExpiraEm(null);
        dispositivo.setUltimaConexao(ZonedDateTime.now());

        return toResponse(dispositivoRepository.save(dispositivo));
    }

    @Transactional
    public DispositivoAtivacaoResponse ping(DispositivoPingRequest request, String ip, String userAgent) {
        if (request == null || request.getDeviceUuid() == null || request.getDeviceUuid().isBlank()) {
            throw new IllegalArgumentException("Device UUID é obrigatório.");
        }

        Dispositivo dispositivo = dispositivoRepository.findByDeviceUuid(request.getDeviceUuid())
                .orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado."));

        dispositivo.setUltimaConexao(ZonedDateTime.now());
        dispositivo.setIp(ip);
        dispositivo.setUserAgent(userAgent);
        dispositivo.setVersaoPlayer(request.getVersaoPlayer());

        return toResponse(dispositivoRepository.save(dispositivo));
    }

    private String gerarPinUnico() {
        String pin;

        do {
            pin = String.valueOf(100000 + RANDOM.nextInt(900000));
        } while (dispositivoRepository.existsByPin(pin));

        return pin;
    }

    private DispositivoAtivacaoResponse toResponse(Dispositivo dispositivo) {
        Tela tela = dispositivo.getTela();

        String codigoPlayer = null;

        if (tela != null) {
            codigoPlayer = tela.getCodigoCurto() != null && !tela.getCodigoCurto().isBlank()
                    ? tela.getCodigoCurto()
                    : tela.getCodigoUnico();
        }

        return DispositivoAtivacaoResponse.builder()
                .id(dispositivo.getId())
                .deviceUuid(dispositivo.getDeviceUuid())
                .pin(dispositivo.getPin())
                .status(dispositivo.getStatus())
                .ativado(dispositivo.getAtivado())
                .expiraEm(dispositivo.getExpiraEm())
                .telaId(tela != null ? tela.getId() : null)
                .telaNome(tela != null ? tela.getNome() : null)
                .codigoUnico(tela != null ? tela.getCodigoUnico() : null)
                .codigoCurto(tela != null ? tela.getCodigoCurto() : null)
                .linkPlayer(codigoPlayer != null ? "/player/" + codigoPlayer : null)
                .build();
    }
}