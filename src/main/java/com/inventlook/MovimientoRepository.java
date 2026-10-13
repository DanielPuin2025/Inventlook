package com.inventlook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {
    // CONSULTA DIRECTA A MYSQL, NOMBRE EXACTO DE TABLA Y COLUMNA
    @Modifying
    @Query(value = "DELETE FROM inventlook.movimientos WHERE fecha_hora < NOW() - INTERVAL 8 DAY", nativeQuery = true)
    void borrarAntiguos8Dias();

    List<Movimiento> findAllByOrderByFechaHoraDesc();
    List<Movimiento> findByProductoIdProductoOrderByFechaHoraDesc(Integer idProducto);
    List<Movimiento> findByTipoMovimientoOrderByFechaHoraDesc(String tipoMovimiento);
    List<Movimiento> findByUsuarioIdUsuarioOrderByFechaHoraDesc(Integer idUsuario);
    long countByTipoMovimiento(String tipoMovimiento);
}