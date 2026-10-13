package com.inventlook;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    // ✅ ÚNICO CAMBIO: llamamos al método con el nombre CORRECTO
    public long contarProductosPorCategoria(Integer idCategoria) {
        return productoRepository.countByCategoria_IdCategoria(idCategoria);
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria buscarPorId(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria).orElse(null);
    }

    public Categoria buscarPorNombre(String nombreCategoria) {
        return categoriaRepository.findByNombreCategoria(nombreCategoria);
    }

    // ✅ BORRAR CATEGORÍA SIN PERDER PRODUCTOS (¡LO NUEVO!)
    public void eliminarPorId(Integer idCategoria) {
        // 1️⃣ PRIMERO: Desasignamos TODOS los productos de esta categoría
        List<Producto> productosDeEstaCategoria = productoRepository.findByCategoria_IdCategoria(idCategoria);
        for (Producto producto : productosDeEstaCategoria) {
            producto.setCategoria(null); // ← quita la categoría, NO borra el producto
            productoRepository.save(producto);
        }
        
        // 2️⃣ LUEGO: Borramos la categoría (ya está libre)
        categoriaRepository.deleteById(idCategoria);
    }

    // ✅ CONTAR TOTAL DE CATEGORÍAS
    public long contarTodas() {
        return categoriaRepository.count();
    }
}