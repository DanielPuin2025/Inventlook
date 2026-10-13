package com.inventlook;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
public class Movimiento {

    // ==================================================
    // TODOS LOS CAMPOS JUNTOS ARRIBA
    // ==================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMovimiento;

    @Column(nullable = false, length = 20)
    private String tipoMovimiento; // ENTRADA / SALIDA / AJUSTE

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 255)
    private String observacion;

    // ✅ Guardamos el nombre del producto para el historial
    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    // ✅ ÚNICO CAMBIO: nullable = true → ya no obligamos a que exista el producto
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = true)
    private Producto producto;

    // RELACIÓN CON USUARIO
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;


    // ==================================================
    // CONSTRUCTORES JUNTOS
    // ==================================================
    public Movimiento() {}

    public Movimiento(String tipoMovimiento,
                      Integer cantidad,
                      LocalDateTime fechaHora,
                      String observacion,
                      Producto producto,
                      Usuario usuario,
                      String nombreProducto) {
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.fechaHora = fechaHora;
        this.observacion = observacion;
        this.producto = producto;
        this.usuario = usuario;
        this.nombreProducto = nombreProducto;
    }


    // ==================================================
    // TODOS LOS GETTERS Y SETTERS JUNTOS
    // ==================================================
    public Integer getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(Integer idMovimiento) { this.idMovimiento = idMovimiento; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}