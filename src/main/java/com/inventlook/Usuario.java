package com.inventlook;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 150, unique = true)
    private String correo;
    // ✅ DEFINITIVO: contrasena — SIN Ñ, ESTÁNDAR
    @Column(nullable = false)
    private String contrasena;
    @Column(length = 20)
    private String telefono;
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    @Column(nullable = false, length = 20)
    private String rol = "EMPLEADO";
    
    // ✅ NUEVO: ESTADO DE LA CUENTA (true = Activo, false = Bloqueado/Inactivo)
    @Column(nullable = false)
    private boolean activo = true;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Producto> productos = new ArrayList<>();
    
    // ✅ Constructor vacío
    public Usuario() {}
    
    // ✅ Constructor completo — AHORA DICE contrasena
    public Usuario(String nombre, String correo, String contrasena, String telefono, LocalDateTime fechaRegistro) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
        this.activo = true; // ✅ Por defecto, toda cuenta nueva nace ACTIVA
    }
    
    // ✅ GETTER Y SETTER NUEVOS: activo
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    // ✅ Getter y Setter UNIFORMES: contrasena / SIN Ñ — así lo pide el sistema
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}