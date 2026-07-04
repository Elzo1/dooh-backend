package com.dooh.signage.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.dooh.signage.model.Campanha;

@Service
public class CampanhaValidador {

    public boolean podeExibir(
            Campanha campanha,
            LocalDate hoje,
            LocalTime agora,
            int diaSemanaAtual
    ) {
        if (campanha == null) return false;

        if (!statusAtivo(campanha)) return false;

        if (!possuiMidia(campanha)) return false;

        if (!dataValida(campanha, hoje)) return false;

        if (!diaSemanaValido(campanha.getDiasSemana(), diaSemanaAtual)) return false;

        return horarioValido(
                campanha.getHoraInicio(),
                campanha.getHoraFim(),
                agora
        );
    }

    private boolean statusAtivo(Campanha campanha) {
        return campanha.getStatus() != null
                && "ATIVA".equalsIgnoreCase(campanha.getStatus().trim());
    }

    private boolean possuiMidia(Campanha campanha) {
        return campanha.getUrlMidia() != null
                && !campanha.getUrlMidia().isBlank();
    }

    private boolean dataValida(Campanha campanha, LocalDate hoje) {
        if (hoje == null) {
            hoje = LocalDate.now();
        }

        LocalDate inicio = campanha.getDataInicio();
        LocalDate fim = campanha.getDataFim();

        if (inicio != null && hoje.isBefore(inicio)) {
            return false;
        }

        if (fim != null && hoje.isAfter(fim)) {
            return false;
        }

        return true;
    }

    private boolean diaSemanaValido(String diasSemana, int diaSemanaAtual) {
        if (diasSemana == null || diasSemana.isBlank()) {
            return true;
        }

        String[] dias = diasSemana.split(",");

        for (String dia : dias) {
            try {
                int diaConvertido = Integer.parseInt(dia.trim());

                if (diaConvertido == diaSemanaAtual) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
                // Ignora valores inválidos no cadastro.
            }
        }

        return false;
    }

    private boolean horarioValido(
            LocalTime inicio,
            LocalTime fim,
            LocalTime agora
    ) {
        if (agora == null) {
            agora = LocalTime.now();
        }

        LocalTime horaInicio = inicio != null ? inicio : LocalTime.MIN;
        LocalTime horaFim = fim != null ? fim : LocalTime.of(23, 59, 59);

        if (horaInicio.equals(horaFim)) {
            return true;
        }

        if (horaInicio.isBefore(horaFim)) {
            return !agora.isBefore(horaInicio)
                    && !agora.isAfter(horaFim);
        }

        // Caso vire o dia. Exemplo: 22:00 até 02:00.
        return !agora.isBefore(horaInicio)
                || !agora.isAfter(horaFim);
    }
}