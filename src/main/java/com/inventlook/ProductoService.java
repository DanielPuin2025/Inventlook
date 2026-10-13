package com.inventlook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final MovimientoService movimientoService;

    public ProductoService(ProductoRepository productoRepository, MovimientoService movimientoService) {
        this.productoRepository = productoRepository;
        this.movimientoService = movimientoService;
    }

    // OBTENER TODOS LOS PRODUCTOS
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // GUARDAR O ACTUALIZAR + CÓDIGO AUTOMÁTICO + REGLA DE ESTADO (SOLO POR "actual")
    public Producto guardar(Producto producto) {
        // CÓDIGO AUTOMÁTICO: SOLO si es producto NUEVO
        if (producto.getIdProducto() == null || producto.getIdProducto() == 0) {
            Long ultimoNumero = productoRepository.obtenerUltimoCodigoNumerico();
            int siguiente = (ultimoNumero == null) ? 1 : (ultimoNumero.intValue() + 1);
            String codigoGenerado = String.format("%03d", siguiente);
            producto.setCodigo(codigoGenerado);
        }

        // ✅ CORREGIDO: ESTADO BASADO SOLO EN "actual"
        int valorActual = producto.getActual() != null ? producto.getActual() : 0;
        
        if (valorActual <= 0) {
            producto.setEstado("Agotado");
        } else if (valorActual < 20) {
            producto.setEstado("Stock bajo");
        } else {
            producto.setEstado("Optimo");
        }

        return productoRepository.save(producto);
    }

    // TRASLADO AUTOMÁTICO DESDE BODEGA
    @Transactional
    public void verificarYTrasladarDesdeBodega(Producto producto, Usuario usuario) {
        if (producto.getActual() == 0 && producto.getBodega() > 0) {
            int cantidadATrasladar = producto.getBodega();
            producto.setActual(cantidadATrasladar);
            producto.setBodega(0);
            productoRepository.save(producto);

            Movimiento traslado = new Movimiento();
            traslado.setTipoMovimiento("TRASLADO");
            traslado.setCantidad(cantidadATrasladar);
            traslado.setFechaHora(LocalDateTime.now());
            traslado.setObservacion("Traslado automático desde bodega a tienda");
            traslado.setProducto(producto);
            traslado.setNombreProducto(producto.getNombre());
            traslado.setUsuario(usuario);
            movimientoService.guardar(traslado);
        }
    }

    // VERIFICAR SI ESTÁ SIN EXISTENCIAS (ambos en 0)
    public boolean estaSinExistencias(Producto producto) {
        Integer actual = producto.getActual();
        Integer bodega = producto.getBodega();
        return (actual == null || actual == 0) && (bodega == null || bodega == 0);
    }

    // REGISTRAR SALIDA DE PRODUCTO
    @Transactional
    public String registrarSalida(Long idProducto, int cantidad, Usuario usuario) {
        Optional<Producto> productoOpt = productoRepository.findById(idProducto);
        if (productoOpt.isEmpty()) {
            return "Producto no encontrado";
        }
        Producto producto = productoOpt.get();
        
        int disponibleActual = producto.getActual() != null ? producto.getActual() : 0;
        int disponibleBodega = producto.getBodega() != null ? producto.getBodega() : 0;
        int totalDisponible = disponibleActual + disponibleBodega;

        if (totalDisponible < cantidad) {
            return "Error: No hay suficiente inventario disponible";
        }

        String observacion = "Salida normal de inventario";

        if (disponibleActual >= cantidad) {
            producto.setActual(disponibleActual - cantidad);
        } else {
            int faltante = cantidad - disponibleActual;
            producto.setActual(0);
            producto.setBodega(disponibleBodega - faltante);
            observacion = "Salida usando inventario de bodega";
        }

        productoRepository.save(producto);

        Movimiento salida = new Movimiento();
        salida.setTipoMovimiento("SALIDA");
        salida.setCantidad(cantidad);
        salida.setFechaHora(LocalDateTime.now());
        salida.setObservacion(observacion);
        salida.setProducto(producto);
        salida.setNombreProducto(producto.getNombre());
        salida.setUsuario(usuario);
        movimientoService.guardar(salida);

        verificarYTrasladarDesdeBodega(producto, usuario);

        if (estaSinExistencias(producto)) {
            return "ALERTA: No hay productos en bodega. El producto está sin existencias.";
        }
        return "Salida registrada correctamente";
    }

    // VALIDAR CÓDIGO DUPLICADO
    public boolean existeCodigo(String codigo, Long idProductoActual) {
        Optional<Producto> existente = productoRepository.findByCodigo(codigo);
        if (existente.isEmpty()) return false;
        return !existente.get().getIdProducto().equals(idProductoActual);
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> listarPorCategoria(Integer idCategoria) {
        return productoRepository.findByCategoria_IdCategoria(idCategoria);
    }

    public List<Producto> listarPorUsuario(Integer idUsuario) {
        return productoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    public List<Producto> buscar(String texto) {
        if (texto == null || texto.isBlank()) return listarTodos();
        return productoRepository.findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(texto.trim(), texto.trim());
    }

    public List<Producto> buscarPorEstado(String estado) {
        return productoRepository.findByEstado(estado);
    }

    // ALERTAS: productos con Actual MENOS DE 20 UNIDADES
    public List<Producto> listarProductosStockBajo() {
        return productoRepository.findByActualLessThan(20);
    }

    public List<Producto> listarSinExistencias() {
        return productoRepository.findByActualAndBodega(0, 0);
    }

    public long contarTodos() {
        return productoRepository.count();
    }
}