package com.dooh.signage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.dooh.signage.dto.EmpresaDashboardResponse;
import com.dooh.signage.dto.EmpresaRequest;
import com.dooh.signage.dto.EmpresaResponse;
import com.dooh.signage.dto.EmpresaResumoResponse;
import com.dooh.signage.service.EmpresaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public List<EmpresaResponse> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status
    ) {
        return empresaService.listar(busca, status);
    }

    @GetMapping("/resumo")
    public EmpresaResumoResponse resumo() {
        return empresaService.resumo();
    }

    @GetMapping("/{id}/dashboard")
    public EmpresaDashboardResponse dashboard(@PathVariable Long id) {
        return empresaService.dashboard(id);
    }

    @GetMapping("/{id}")
    public EmpresaResponse buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    @PostMapping
    public EmpresaResponse criar(@RequestBody EmpresaRequest request) {
        return empresaService.criar(request);
    }

    @PutMapping("/{id}")
    public EmpresaResponse atualizar(@PathVariable Long id, @RequestBody EmpresaRequest request) {
        return empresaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void desativar(@PathVariable Long id) {
        empresaService.desativar(id);
    }
}