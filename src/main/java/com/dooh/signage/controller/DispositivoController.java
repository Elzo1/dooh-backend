package com.dooh.signage.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooh.signage.dto.DispositivoAtivacaoResponse;
import com.dooh.signage.dto.DispositivoPingRequest;
import com.dooh.signage.dto.SolicitarPinRequest;
import com.dooh.signage.dto.VincularDispositivoRequest;
import com.dooh.signage.service.DispositivoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dispositivos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DispositivoController {

    private final DispositivoService dispositivoService;

    @PostMapping("/solicitar-pin")
    public DispositivoAtivacaoResponse solicitarPin(
            @RequestBody SolicitarPinRequest request,
            HttpServletRequest servletRequest
    ) {
        return dispositivoService.solicitarPin(
                request,
                servletRequest.getRemoteAddr(),
                servletRequest.getHeader("User-Agent")
        );
    }

    @GetMapping("/pin/{pin}")
    public DispositivoAtivacaoResponse buscarPorPin(@PathVariable String pin) {
        return dispositivoService.buscarPorPin(pin);
    }

    @GetMapping("/device/{deviceUuid}")
    public DispositivoAtivacaoResponse buscarPorDeviceUuid(@PathVariable String deviceUuid) {
        return dispositivoService.buscarPorDeviceUuid(deviceUuid);
    }

    @PostMapping("/vincular")
    public DispositivoAtivacaoResponse vincular(@RequestBody VincularDispositivoRequest request) {
        return dispositivoService.vincular(request);
    }

    @PostMapping("/ping")
    public DispositivoAtivacaoResponse ping(
            @RequestBody DispositivoPingRequest request,
            HttpServletRequest servletRequest
    ) {
        return dispositivoService.ping(
                request,
                servletRequest.getRemoteAddr(),
                servletRequest.getHeader("User-Agent")
        );
    }
}