package com.inventlook;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/categorias")
    public String mostrarCategorias(HttpSession sesion, Model model) {
        if (sesion.getAttribute("nombreUsuario") == null) {
            return "redirect:/login";
        }

        List<Categoria> listaCategorias = categoriaService.listarTodas();
        System.out.println("✅ Categorías cargadas: " + 
        listaCategorias.size() + " → " + listaCategorias);
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("totalCategorias", listaCategorias.size());

        return "categorias";
    }

    @GetMapping("/agregar-categoria")
    public String formularioNueva(HttpSession sesion) {
        if (sesion.getAttribute("nombreUsuario") == null || !"ADMINISTRADOR".equals(sesion.getAttribute("rolUsuario"))) {
            return "redirect:/categorias?error=sinPermiso";
        }
        return "agregar-categoria";
    }

    @PostMapping("/guardar-categoria")
    public String guardarCategoria(Categoria categoria, HttpSession sesion) {
        if (sesion.getAttribute("nombreUsuario") == null || !"ADMINISTRADOR".equals(sesion.getAttribute("rolUsuario"))) {
            return "redirect:/categorias?error=sinPermiso";
        }

        Categoria existente = categoriaService.buscarPorNombre(categoria.getNombreCategoria());
        if (existente != null) {
            return "redirect:/agregar-categoria?error=duplicado";
        }

        categoriaService.guardar(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/eliminar-categoria/{id}")
    public String eliminarCategoria(@PathVariable Integer id, HttpSession sesion) {
        if (sesion.getAttribute("nombreUsuario") == null || !"ADMINISTRADOR".equals(sesion.getAttribute("rolUsuario"))) {
            return "redirect:/categorias?error=sinPermiso";
        }
        categoriaService.eliminarPorId(id);
        return "redirect:/categorias";
    }
}