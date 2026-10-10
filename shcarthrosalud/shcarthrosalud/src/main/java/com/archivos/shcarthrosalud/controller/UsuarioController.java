package com.archivos.shcarthrosalud.controller;

import com.archivos.shcarthrosalud.dto.UsuarioDTO;
import com.archivos.shcarthrosalud.entity.Usuario;
import com.archivos.shcarthrosalud.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend (Angular, React, etc.)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }
}
