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
        // 1. Verificar si el correo ya existe
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new RuntimeException("El correo ya está registrado en el sistema");
        }
        // ✅ Verificar si el teléfono ya existe
        if (usuarioRepository.existsByTelefono(telefono)) {
            throw new RuntimeException("El número de teléfono ya está registrado en el sistema");
        }
        // 2. Creamos el usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());
        // 3. Guardamos
        return usuarioRepository.save(nuevoUsuario);
    }

    // ✅ Listar todos — ORDENADOS ALFABÉTICAMENTE (A-Z)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNombreAsc();
    }

    // ✅ Buscar por correo
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // ✅ VALIDAR INICIO DE SESIÓN: TELÉFONO + CONTRASEÑA
    public boolean validarInicioSesion(String telefono, String contrasena) {
        Optional<Usuario> usuarioBuscado = usuarioRepository.findByTelefono(telefono);
        if (usuarioBuscado.isEmpty()) {
            return false;
        }
        Usuario usuarioReal = usuarioBuscado.get();
        return usuarioReal.getContrasena().equals(contrasena);
    }

    // ✅ Buscar usuario por teléfono
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

    // ✅ ACTUALIZAR DATOS DEL ADMINISTRADOR
    public Usuario actualizarAdministrador(Integer idUsuario, String nombre, String correo, String telefono, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el correo ya existe en OTRO usuario
        if (!usuario.getCorreo().equals(correo) && usuarioRepository.existsByCorreo(correo)) {
            throw new RuntimeException("El correo ya está registrado por otra persona");
        }

        // Verificar si el teléfono ya existe en OTRO usuario
        String telefonoAnterior = usuario.getTelefono();
        if (!telefono.equals(telefonoAnterior) && usuarioRepository.existsByTelefono(telefono)) {
            throw new RuntimeException("El número de teléfono ya está registrado por otra persona");
        }

        // Actualizar datos
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);

        // Solo cambiar contraseña si NO está vacía
        if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
            if (!nuevaContrasena.matches("[0-9]+") || nuevaContrasena.length() < 6) {
                throw new RuntimeException("La contraseña debe tener solo números y mínimo 6 dígitos");
            }
            usuario.setContrasena(nuevaContrasena);
        }

        return usuarioRepository.save(usuario);
    }
}