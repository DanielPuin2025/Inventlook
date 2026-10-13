package com.inventlook;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public DashboardController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession sesion, Model modelo) {
        // 🔒 Seguridad: si no hay sesión, va al login
        if (sesion.getAttribute("nombreUsuario") == null) {
            return "redirect:/login";
        }

        // ✅ DATOS EN TIEMPO REAL DESDE LA BASE DE DATOS
        long totalProductos = productoService.contarTodos();
        long totalCategorias = categoriaService.contarTodas();
        long totalAlertas = productoService.listarProductosStockBajo().size();

        // ✅ Enviamos los números al HTML
        modelo.addAttribute("totalProductos", totalProductos);
        modelo.addAttribute("totalCategorias", totalCategorias);
        modelo.addAttribute("totalAlertas", totalAlertas);

        return "dashboard";
    }
}