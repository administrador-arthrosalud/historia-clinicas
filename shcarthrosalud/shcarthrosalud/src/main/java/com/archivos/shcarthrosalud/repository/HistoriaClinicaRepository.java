package com.archivos.shcarthrosalud.repository;

import com.archivos.shcarthrosalud.entity.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica,Long> {

    // Consulta optimizada para soportar 3, 4 o más dígitos sin problemas de ordenación alfabética
    @Query("SELECT h.codigoHc FROM HistoriaClinica h " +
            "WHERE h.codigoHc LIKE CONCAT(:letra, '%') " +
            "ORDER BY LENGTH(h.codigoHc) DESC, h.codigoHc DESC LIMIT 1")
    Optional<String> obtenerUltimoCodigoPorLetra(@Param("letra") String letra);

    long countByApellidoPaternoStartingWith(String prefijo);


    @Query("SELECT h FROM HistoriaClinica h WHERE " +
            "(:apellidoPaterno IS NULL OR LOWER(h.apellidoPaterno) LIKE LOWER(CONCAT('%', :apellidoPaterno, '%'))) AND " +
            "(:apellidoMaterno IS NULL OR LOWER(h.apellidoMaterno) LIKE LOWER(CONCAT('%', :apellidoMaterno, '%'))) AND " +
            "(:nombres IS NULL OR LOWER(h.nombres) LIKE LOWER(CONCAT('%', :nombres, '%')))")
    List<HistoriaClinica> buscarPorNombreCompleto(
            @Param("apellidoPaterno") String apellidoPaterno,
            @Param("apellidoMaterno") String apellidoMaterno,
            @Param("nombres") String nombres
    );

    boolean existsByDni(String dni);
}
