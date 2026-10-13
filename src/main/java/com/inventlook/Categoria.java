package com.inventlook;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria; // ✅ Nombre en Java: idCategoria

    @Column(name = "nombre_categoria", nullable = false, unique = true)
    private String nombreCategoria; // ✅ Nombre en Java: nombreCategoria

    // Constructor vacío
    public Categoria() {}

    // Getters y Setters CON ESTOS NOMBRES EXACTOS
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
}