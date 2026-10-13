package com.inventlook;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Buscar usuario por correo
    Optional<Usuario> findByCorreo(String correo);
    
    // Verificar si ya existe ese correo registrado
    boolean existsByCorreo(String correo);
    
    // Buscar usuario por nombre
    Optional<Usuario> findByNombre(String nombre);
    
    // ✅ NUEVO: Buscar usuario por NUMERO DE TELEFONO (para inicio de sesion)
    Optional<Usuario> findByTelefono(String telefono);
    
    // ✅ NUEVO: Verificar si ya existe ese telefono registrado
    boolean existsByTelefono(String telefono);
    
    // Listar TODOS los usuarios ordenados por nombre (A-Z)
    List<Usuario> findAllByOrderByNombreAsc();
}