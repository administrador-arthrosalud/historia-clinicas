package com.archivos.shcarthrosalud.services;

import com.archivos.shcarthrosalud.dto.UsuarioDTO;
import com.archivos.shcarthrosalud.entity.Usuario;
import com.archivos.shcarthrosalud.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario guardarUsuario(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        return usuarioRepository.save(usuario);
    }
}