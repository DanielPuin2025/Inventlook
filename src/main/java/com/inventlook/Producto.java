package com.inventlook;

import jakarta.persistence.*;

@Entity
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = true)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false)
    private Integer actual = 0;

    @Column(nullable = false)
    private Integer bodega = 0;

    @Column(nullable = false, length = 20)
    private String estado = "Optimo";

    @Column(length = 100)
    private String proveedor;

    public Producto() {}

    // ✅ CORREGIDO: Ahora evalúa SOLO el campo "actual"
    public void actualizarEstadoPorCantidad() {
        int valorActual = (actual != null) ? actual : 0;
        
        if (valorActual <= 0) {
            this.estado = "Agotado";
        } else if (valorActual < 20) {
            this.estado = "Stock bajo";
        } else {
            this.estado = "Optimo";
        }
    }

    // Getters y Setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Integer getActual() { return actual; }
    public void setActual(Integer actual) {
        this.actual = actual;
        actualizarEstadoPorCantidad(); // Recalcula estado automáticamente
    }

    public Integer getBodega() { return bodega; }
    public void setBodega(Integer bodega) {
        this.bodega = bodega;
        // Nota: ya NO recalcula estado al cambiar bodega
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
}