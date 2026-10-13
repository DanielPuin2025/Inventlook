package com.inventlook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Contar productos de UNA CATEGORIA
    long countByCategoria_IdCategoria(Integer idCategoria);

    // Traer SOLO los productos de UNA CATEGORIA
    List<Producto> findByCategoria_IdCategoria(Integer idCategoria);

    // Traer SOLO los productos de UN USUARIO
    List<Producto> findByUsuario_IdUsuario(Integer idUsuario);

    // NUEVO: Productos sin existencias (Actual = 0 Y Bodega = 0)
    List<Producto> findByActualAndBodega(int actual, int bodega);

    // NUEVO: Productos con stock bajo en Actual (para alertas)
    List<Producto> findByActualLessThan(int cantidad);

    // Buscar 1 producto por su CODIGO
    Optional<Producto> findByCodigo(String codigo);

    // Buscador por NOMBRE o CODIGO
    List<Producto> findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(String nombre, String codigo);

    // FILTRO POR ESTADO
    List<Producto> findByEstado(String estado);

    // OBTENER EL ULTIMO CODIGO PARA AUTO-GENERAR 001, 002...
    @Query("SELECT MAX(CAST(p.codigo AS long)) FROM Producto p")
    Long obtenerUltimoCodigoNumerico();
}