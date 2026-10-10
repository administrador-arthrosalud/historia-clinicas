package com.archivos.shcarthrosalud.services.impl;

import com.archivos.shcarthrosalud.dto.HistoriaClinicaDTO;
import com.archivos.shcarthrosalud.entity.HistoriaClinica;
import com.archivos.shcarthrosalud.entity.Usuario;
import com.archivos.shcarthrosalud.repository.HistoriaClinicaRepository;
import com.archivos.shcarthrosalud.repository.UsuarioRepository;
import com.archivos.shcarthrosalud.services.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository historiaClinicaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public HistoriaClinica crearHistoriaClinica(HistoriaClinica historia) {
        // 1. Obtienes la primera letra del apellido paterno (ejemplo: "Perez" -> "P")
        String prefijo = historia.getApellidoPaterno().substring(0, 1).toUpperCase();

        // 2. ¡AQUÍ ES DONDE LLAMAS A TU MÉTODO DEL REPOSITORY!
        long totalExistentes = historiaClinicaRepository.countByApellidoPaternoStartingWith(prefijo);

        // 3. Con el resultado que te devuelve SQL Server, generas el código (ejemplo: P001, P002)
        String codigoGenerado = String.format("%s%03d", prefijo, totalExistentes + 1);
        historia.setCodigoHc(codigoGenerado);

        // 4. Guardas la historia en la base de datos
        return historiaClinicaRepository.save(historia);
    }

    @Override
    public List<HistoriaClinica> listarHistoriaClinica() {
        return historiaClinicaRepository.findAll();
    }



    @Override
    public HistoriaClinica buscarPorDni(String dni) {
        return null;
    }

    @Override
    public HistoriaClinica registrarHistoriaClinica(HistoriaClinicaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.idUsuario)
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado con id"+dto.idUsuario));

        if (historiaClinicaRepository.existsByDni(dto.getDni())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe una historia clínica registrada con el DNI: " + dto.getDni()
            );
        }
        HistoriaClinica hc = new HistoriaClinica();
        hc.setUsuario(usuario);
        hc.setDni(dto.dni);
        hc.setApellidoPaterno(dto.apellidoPaterno);
        hc.setApellidoMaterno(dto.apellidoMaterno);
        hc.setNombres(dto.nombres);


        // 3. Generar el código correlativo de Historia Clínica
        String nuevoCodigo = generarCodigoHc(dto.getApellidoPaterno());
        hc.setCodigoHc(nuevoCodigo);

        return historiaClinicaRepository.save(hc);
    }

    @Override
    public List<HistoriaClinica> buscarPorNombreCompleto(String apellidoPaterno, String apellidoMaterno, String nombres) {
        return historiaClinicaRepository.buscarPorNombreCompleto(apellidoPaterno, apellidoMaterno, nombres);
    }


    // Método auxiliar para generar la nomenclatura (Ej: A012)
    private String generarCodigoHc(String apellidoPaterno) {
        if (apellidoPaterno == null || apellidoPaterno.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido paterno no puede estar vacío");
        }

        // Obtener la primera letra en mayúscula
        String primeraLetra = apellidoPaterno.trim().substring(0, 1).toUpperCase();

        // Buscar el último código que empiece con esa letra (ej: "A011")
        Optional<String> ultimoCodigoOpt = historiaClinicaRepository.obtenerUltimoCodigoPorLetra(primeraLetra);

        int siguienteNumero = 1;

        if (ultimoCodigoOpt.isPresent()) {
            String ultimoCodigo = ultimoCodigoOpt.get(); // ej: "A011"
            try {
                // Extraer la parte numérica cortando el primer carácter
                String numeroStr = ultimoCodigo.substring(1);
                siguienteNumero = Integer.parseInt(numeroStr) + 1; // 11 + 1 = 12
            } catch (NumberFormatException e) {
                siguienteNumero = 1;
            }
        }

        // Formatear el número con 3 dígitos rellenos de ceros (ej: 12 -> "012")
        return primeraLetra + String.format("%03d", siguienteNumero); // Resultado: "A012"
    }




}
