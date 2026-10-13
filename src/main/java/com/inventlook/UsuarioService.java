package com.inventlook;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ Registrar nuevo usuario
    public Usuario registrarUsuario(String nombre, String correo, String contrasena, String telefono) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new RuntimeException("El correo ya esta registrado en el sistema");
        }
        if (telefono != null && !telefono.isBlank() && usuarioRepository.existsByTelefono(telefono)) {
            throw new RuntimeException("El numero de telefono ya esta registrado en el sistema");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());
        return usuarioRepository.save(nuevoUsuario);
    }

    // ✅ NUEVO: ACTUALIZAR DATOS DEL ADMINISTRADOR
    public Usuario actualizarUsuario(Integer idUsuario, String nombre, String correo, String telefono, String contrasenaNueva) {
        // 1. Buscar el usuario
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new RuntimeException("El usuario no existe en el sistema");
        }

        // 2. Verificar que el correo no lo use OTRO usuario
        Optional<Usuario> porCorreo = usuarioRepository.findByCorreo(correo);
        if (porCorreo.isPresent() && !porCorreo.get().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("El correo ya esta registrado por otro usuario");
        }

        // 3. Verificar que el teléfono no lo use OTRO usuario
        if (telefono != null && !telefono.isBlank()) {
            Optional<Usuario> porTelefono = usuarioRepository.findByTelefono(telefono);
            if (porTelefono.isPresent() && !porTelefono.get().getIdUsuario().equals(idUsuario)) {
                throw new RuntimeException("El numero de telefono ya esta registrado por otro usuario");
            }
        }

        // 4. Actualizar los datos
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);

        // 5. Solo cambiar la contraseña si se envió una nueva
        if (contrasenaNueva != null && !contrasenaNueva.isBlank()) {
            usuario.setContrasena(contrasenaNueva);
        }

        // 6. Guardar y devolver
        return usuarioRepository.save(usuario);
    }

    // ✅ Listar todos — ORDENADOS ALFABETICAMENTE (A-Z)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNombreAsc();
    }

    // ✅ Buscar por correo
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // ✅ VALIDAR INICIO DE SESION: TELEFONO + CONTRASEÑA
    public boolean validarInicioSesion(String telefono, String contrasena) {
        Optional<Usuario> usuarioBuscado = usuarioRepository.findByTelefono(telefono);
        if (usuarioBuscado.isEmpty()) {
            return false;
        }
        Usuario usuarioReal = usuarioBuscado.get();
        return usuarioReal.getContrasena().equals(contrasena);
    }

    // ✅ Buscar usuario por telefono
    public Usuario buscarPorTelefono(String telefono) {
        return usuarioRepository.findByTelefono(telefono).orElse(null);
    }

    // ✅ Buscar por nombre
    public Usuario buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre).orElse(null);
    }

    // ✅ Eliminar usuario por ID
    public void eliminarUsuario(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    // ✅ Buscar usuario por ID
    public Usuario buscarPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario).orElse(null);
    }
}