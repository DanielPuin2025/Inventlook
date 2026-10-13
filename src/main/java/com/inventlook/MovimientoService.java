package com.inventlook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoService {
    private final MovimientoRepository movimientoRepository;
    public MovimientoService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    // TRANSACCION EXPLICITA PARA ESCRITURA (SOLUCIONA "SOLO LECTURA")
    @Transactional
    public void borrarMovimientosViejos() {
        movimientoRepository.borrarAntiguos8Dias();
    }

    // AL CARGAR LA PAGINA: PRIMERO BORRA, LUEGO MUESTRA
    @Transactional
    public List<Movimiento> listarRecientes() {
        borrarMovimientosViejos();
        return movimientoRepository.findAllByOrderByFechaHoraDesc();
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarTodos() {
        return movimientoRepository.findAllByOrderByFechaHoraDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Movimiento> buscarPorId(Integer id) {
        return movimientoRepository.findById(id);
    }

    @Transactional
    public Movimiento guardar(Movimiento movimiento) {
        return movimientoRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarPorProducto(Integer idProducto) {
        return movimientoRepository.findByProductoIdProductoOrderByFechaHoraDesc(idProducto);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarPorTipo(String tipo) {
        return movimientoRepository.findByTipoMovimientoOrderByFechaHoraDesc(tipo);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarPorUsuario(Integer idUsuario) {
        return movimientoRepository.findByUsuarioIdUsuarioOrderByFechaHoraDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public long contarPorTipo(String tipo) {
        return movimientoRepository.countByTipoMovimiento(tipo);
    }
}