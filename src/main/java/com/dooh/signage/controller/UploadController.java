package com.dooh.signage.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dooh.signage.dto.MidiaResponse;
import com.dooh.signage.model.Midia;
import com.dooh.signage.repository.MidiaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UploadController {

    private final MidiaRepository midiaRepository;

    private final Path uploadPath = Paths.get("uploads");

    @GetMapping
    public List<MidiaResponse> listar() {
        return midiaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public MidiaResponse upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String original = file.getOriginalFilename();
        String extensao = extrairExtensao(original);

        String nomeArquivo = UUID.randomUUID() + extensao;
        Path destino = uploadPath.resolve(nomeArquivo);

        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        String tipo = file.getContentType();
        String categoria = definirCategoria(tipo);

        Midia midia = Midia.builder()
                .nomeOriginal(original)
                .nomeArquivo(nomeArquivo)
                .nomeExibicao(removerExtensao(original))
                .tipo(tipo)
                .categoria(categoria)
                .tamanhoBytes(file.getSize())
                .url("/uploads/" + nomeArquivo)
                .thumbnailUrl(definirThumbnail(categoria, nomeArquivo))
                .largura(null)
                .altura(null)
                .duracaoSegundos(null)
                .status("DISPONIVEL")
                .build();

        return toResponse(midiaRepository.save(midia));
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) throws Exception {
        Midia midia = midiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mídia não encontrada."));

        Path arquivo = uploadPath.resolve(midia.getNomeArquivo());

        Files.deleteIfExists(arquivo);
        midiaRepository.delete(midia);
    }

    private MidiaResponse toResponse(Midia midia) {
        return MidiaResponse.builder()
                .id(midia.getId())
                .nomeOriginal(midia.getNomeOriginal())
                .nomeArquivo(midia.getNomeArquivo())
                .nomeExibicao(midia.getNomeExibicao())
                .tipo(midia.getTipo())
                .categoria(midia.getCategoria())
                .tamanhoBytes(midia.getTamanhoBytes())
                .url(midia.getUrl())
                .thumbnailUrl(midia.getThumbnailUrl())
                .largura(midia.getLargura())
                .altura(midia.getAltura())
                .duracaoSegundos(midia.getDuracaoSegundos())
                .status(midia.getStatus())
                .createdAt(midia.getCreatedAt())
                .build();
    }

    private String extrairExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return "";
        }

        return nomeArquivo.substring(nomeArquivo.lastIndexOf("."));
    }

    private String removerExtensao(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            return "Mídia sem nome";
        }

        if (!nomeArquivo.contains(".")) {
            return nomeArquivo;
        }

        return nomeArquivo.substring(0, nomeArquivo.lastIndexOf("."));
    }

    private String definirCategoria(String tipo) {
        if (tipo == null) {
            return "ARQUIVO";
        }

        if (tipo.startsWith("image/")) {
            return "IMAGEM";
        }

        if (tipo.startsWith("video/")) {
            return "VIDEO";
        }

        return "ARQUIVO";
    }

    private String definirThumbnail(String categoria, String nomeArquivo) {
        if ("IMAGEM".equals(categoria)) {
            return "/uploads/" + nomeArquivo;
        }

        return null;
    }
}