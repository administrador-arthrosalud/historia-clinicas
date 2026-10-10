package com.archivos.shcarthrosalud.services;

import com.archivos.shcarthrosalud.dto.HistoriaClinicaDTO;
import com.archivos.shcarthrosalud.entity.HistoriaClinica;
import com.archivos.shcarthrosalud.entity.Usuario;
import com.archivos.shcarthrosalud.repository.HistoriaClinicaRepository;
import com.archivos.shcarthrosalud.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HistoriaClinicaService{

    HistoriaClinica crearHistoriaClinica(HistoriaClinica historiaClinica);

    List<HistoriaClinica> listarHistoriaClinica();

    HistoriaClinica buscarPorDni(String dni);

    HistoriaClinica registrarHistoriaClinica(HistoriaClinicaDTO dto);

    List<HistoriaClinica> buscarPorNombreCompleto(String apellidoPaterno, String apellidoMaterno, String nombres);

}
