package com.archivos.shcarthrosalud.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoriaClinicaDTO {
    public Long idUsuario;
    public String dni;
    public String codigoHc;
    public String apellidoPaterno;
    public String apellidoMaterno;
    public String nombres;
}
