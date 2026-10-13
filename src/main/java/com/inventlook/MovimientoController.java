package com.inventlook;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {
    private final MovimientoService movimientoService;
    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    // VER TODOS LOS MOVIMIENTOS (duracion 8 dias, agrupados por fecha)
    @GetMapping
    public String verMovimientos(HttpSession sesion, Model modelo) {
        // Seguridad: si no estas logueado, va al login
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return "redirect:/login";
        }

        // Primero borra los movimientos viejos (mas de 8 dias)
        movimientoService.borrarMovimientosViejos();

        // Traemos todos los movimientos recientes
        List<Movimiento> todosMovimientos = movimientoService.listarRecientes();

        // Agrupamos los movimientos por fecha (del mas reciente al mas viejo)
        Map<LocalDate, List<Movimiento>> movimientosPorFecha = new LinkedHashMap<>();
        for (Movimiento mov : todosMovimientos) {
            LocalDate fecha = mov.getFechaHora().toLocalDate();
            movimientosPorFecha.computeIfAbsent(fecha, k -> new ArrayList<>()).add(mov);
        }

        // Enviamos los datos agrupados a la vista
        modelo.addAttribute("movimientosPorFecha", movimientosPorFecha);
        modelo.addAttribute("nombreUsuario", nombreUsuario);
        modelo.addAttribute("iniciales", nombreUsuario.substring(0, 2).toUpperCase());
        return "movimientos";
    }

    // VER MOVIMIENTOS DE UN SOLO PRODUCTO (duracion 8 dias, agrupados por fecha)
    @GetMapping("/producto/{id}")
    public String verPorProducto(@PathVariable Integer id, HttpSession sesion, Model modelo) {
        String nombreUsuario = (String) sesion.getAttribute("nombreUsuario");
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return "redirect:/login";
        }

        // Primero limpiamos los viejos (mas de 8 dias)
        movimientoService.borrarMovimientosViejos();

        // Traemos los movimientos de este producto
        List<Movimiento> movimientosProducto = movimientoService.listarPorProducto(id);

        // Agrupamos por fecha
        Map<LocalDate, List<Movimiento>> movimientosPorFecha = new LinkedHashMap<>();
        for (Movimiento mov : movimientosProducto) {
            LocalDate fecha = mov.getFechaHora().toLocalDate();
            movimientosPorFecha.computeIfAbsent(fecha, k -> new ArrayList<>()).add(mov);
        }

        modelo.addAttribute("movimientosPorFecha", movimientosPorFecha);
        modelo.addAttribute("nombreUsuario", nombreUsuario);
        modelo.addAttribute("iniciales", nombreUsuario.substring(0, 2).toUpperCase());
        return "movimientos";
    }
}