package com.dooh.signage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dooh.signage.dto.CampanhaRequest;
import com.dooh.signage.dto.CampanhaResumoResponse;
import com.dooh.signage.service.CampanhaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/campanhas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CampanhaController {

    private final CampanhaService campanhaService;

    @GetMapping
    public ResponseEntity<List<CampanhaResumoResponse>> listar() {
        return ResponseEntity.ok(campanhaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampanhaResumoResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                campanhaService.buscarPorId(id)
        );
    }

    @PostMapping
    public ResponseEntity<CampanhaResumoResponse> criar(
            @RequestBody CampanhaRequest request) {

        return ResponseEntity.ok(
                campanhaService.criar(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampanhaResumoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody CampanhaRequest request) {

        return ResponseEntity.ok(
                campanhaService.atualizar(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CampanhaResumoResponse> alterarStatus(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                campanhaService.alternarStatus(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        campanhaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

}