package br.com.fiap.agrotech.controller;

import br.com.fiap.agrotech.dto.PrevisaoSateliteDto;
import br.com.fiap.agrotech.dto.RegistroSoloDto;
import br.com.fiap.agrotech.model.PrevisaoSatelite;
import br.com.fiap.agrotech.repository.PrevisaoSateliteRepository;
import br.com.fiap.agrotech.service.AgroInteligenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/agro")
@RequiredArgsConstructor
@Tag(name = "Agricultura de Precisão", description = "Endpoints para integração IoT e Satélites")
public class AgroController {

    private final AgroInteligenciaService inteligenciaService;
    private final PrevisaoSateliteRepository sateliteRepository;

    @PostMapping("/solo")
    @Operation(summary = "Recebe dados do ESP32", description = "Avalia umidade/temperatura e decide sobre bloqueio de rega")
    public ResponseEntity<String> receberDadosSolo(@Valid @RequestBody RegistroSoloDto dto) {
        String resultadoAnalise = inteligenciaService.processarAnaliseSolo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultadoAnalise);
    }

    @PostMapping("/satelite")
    @Operation(summary = "Atualiza previsão orbital", description = "Recebe dados climáticos da NASA/ESA sobre chuva iminente")
    public ResponseEntity<PrevisaoSatelite> atualizarDadosSatelite(@Valid @RequestBody PrevisaoSateliteDto dto) {
        PrevisaoSatelite novaPrevisao = new PrevisaoSatelite(
                null,
                dto.getRegiao(),
                dto.getChuvaIminente(),
                LocalDate.now()
        );
        PrevisaoSatelite salva = sateliteRepository.save(novaPrevisao);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }
}