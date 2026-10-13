package com.inventlook;

import org.springframework.data.jpa.repository.JpaRepository;

// ✅ USAMOS Integer PORQUE TU id_categoria ES NÚMERO ENTERO
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // ✅ Ya tienes gratis: listar, guardar, editar, eliminar, buscar por ID

     // ✅ SOLO AGREGA ESTA LÍNEA NUEVA (sirve para NO repetir nombres de categorías)
    Categoria findByNombreCategoria(String nombreCategoria);
}