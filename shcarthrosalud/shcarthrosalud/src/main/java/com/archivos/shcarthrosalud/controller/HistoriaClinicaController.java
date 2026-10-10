package com.archivos.shcarthrosalud.controller;

import com.archivos.shcarthrosalud.dto.HistoriaClinicaDTO;
import com.archivos.shcarthrosalud.entity.HistoriaClinica;
import com.archivos.shcarthrosalud.services.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrar-historia-clinica")
@CrossOrigin(origins = "*") // Permite peticiones desde frontend (React, Angular, JS, etc.)
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    // POST: Registrar una nueva Historia Clínica (se genera el código A001, A002... automáticamente)
    @PostMapping
    public ResponseEntity<?> registrarHistoriaClinica(@RequestBody HistoriaClinicaDTO dto) {
        try {
            HistoriaClinica nuevaHc = historiaClinicaService.registrarHistoriaClinica(dto);
            return new ResponseEntity<>(nuevaHc, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // GET: Listar todas las historias clínicas
    @GetMapping
    public ResponseEntity<List<HistoriaClinica>> listarHistorias() {
        List<HistoriaClinica> lista = historiaClinicaService.listarHistoriaClinica();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar historia clínica por DNI
    @GetMapping("/buscarpordni")
    public ResponseEntity<?> buscarPorDni(@RequestParam String dni) {
        try {
            HistoriaClinica hc = historiaClinicaService.buscarPorDni(dni);
            if (hc != null) {
                return ResponseEntity.ok(hc);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la historia clínica con el DNI: " + dni);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // GET: Buscar por apellido paterno, apellido materno y/o nombres
// Ejemplo: /api/historias-clinicas/buscar-nombre?apellidoPaterno=Andrade&apellidoMaterno=López&nombres=Carlos
    @GetMapping("/buscar-nombre")
    public ResponseEntity<List<HistoriaClinica>> buscarPorNombreCompleto(
            @RequestParam(required = false) String apellidoPaterno,
            @RequestParam(required = false) String apellidoMaterno,
            @RequestParam(required = false) String nombres) {

        List<HistoriaClinica> resultados = historiaClinicaService.buscarPorNombreCompleto(apellidoPaterno, apellidoMaterno, nombres);
        return ResponseEntity.ok(resultados);
    }
}