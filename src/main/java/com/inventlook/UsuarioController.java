package com.inventlook;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Abre la ventana de INICIAR SESION
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/login";
    }

    // ✅ INICIO DE SESION CON NUMERO DE TELEFONO + CONTRASEÑA
    @PostMapping("/iniciar-sesion")
    public String iniciarSesion(
            @RequestParam String telefono,
            @RequestParam String contrasena,
            Model model,
            HttpSession sesion) {
        
        boolean accesoValido = usuarioService.validarInicioSesion(telefono, contrasena);
        if (accesoValido) {
            Usuario usuarioLogueado = usuarioService.buscarPorTelefono(telefono);
            
            sesion.setAttribute("idUsuario", usuarioLogueado.getIdUsuario());
            sesion.setAttribute("nombreUsuario", usuarioLogueado.getNombre());
            sesion.setAttribute("rolUsuario", usuarioLogueado.getRol());
            sesion.setAttribute("telefonoUsuario", telefono);
            
            return "redirect:/dashboard";
        } else {
            model.addAttribute("mensaje", "Error: Número de teléfono o contraseña incorrectos");
            return "login";
        }
    }

    // =============================================
    // GESTIÓN DE CUENTAS (SOLO ADMINISTRADOR)
    // =============================================

    // Ver lista de cuentas
    @GetMapping("/cuentas")
    public String gestionarCuentas(HttpSession sesion, Model modelo, RedirectAttributes ra) {
        String rol = (String) sesion.getAttribute("rolUsuario");
        if (rol == null || !rol.equals("ADMINISTRADOR")) {
            ra.addFlashAttribute("error", "Acceso exclusivo para Administradores");
            return "redirect:/dashboard";
        }
        modelo.addAttribute("listaUsuarios", usuarioService.listarTodos());
        return "cuentas";
    }

    // Eliminar una cuenta
    @GetMapping("/cuentas/eliminar/{id}")
    public String eliminarCuenta(@PathVariable Integer id,
                                  HttpSession sesion,
                                  RedirectAttributes ra) {
        String rol = (String) sesion.getAttribute("rolUsuario");
        if (rol == null || !rol.equals("ADMINISTRADOR")) {
            ra.addFlashAttribute("error", "Acceso exclusivo para Administradores");
            return "redirect:/dashboard";
        }
        
        Integer miId = (Integer) sesion.getAttribute("idUsuario");
        if (id.equals(miId)) {
            ra.addFlashAttribute("error", "No puedes borrar tu propia cuenta");
            return "redirect:/cuentas";
        }
        
        usuarioService.eliminarUsuario(id);
        ra.addFlashAttribute("exito", "Cuenta eliminada correctamente — ya no tiene acceso al sistema");
        return "redirect:/cuentas";
    }

    // ✅ ACTUALIZAR DATOS DEL ADMINISTRADOR
    @PostMapping("/cuentas/actualizar-administrador")
    public String actualizarAdministrador(
            @RequestParam Integer idUsuario,
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam(required = false) String nuevaContrasena,
            HttpSession sesion,
            RedirectAttributes ra) {
        
        // Verificar que sea administrador
        String rol = (String) sesion.getAttribute("rolUsuario");
        if (rol == null || !rol.equals("ADMINISTRADOR")) {
            ra.addFlashAttribute("error", "Acceso denegado");
            return "redirect:/dashboard";
        }
        
        try {
            // Llamar al servicio para actualizar
            usuarioService.actualizarAdministrador(idUsuario, nombre, correo, telefono, nuevaContrasena);
            
            // Si el administrador se cambia su propio nombre, actualizarlo en la sesión
            Integer miId = (Integer) sesion.getAttribute("idUsuario");
            if (idUsuario.equals(miId)) {
                sesion.setAttribute("nombreUsuario", nombre);
            }
            
            ra.addFlashAttribute("exito", "Datos actualizados correctamente ✅");
        } 
        catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cuentas";
    }
}