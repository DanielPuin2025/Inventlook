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

    // ✅ INICIO DE SESION CON NUMERO DE TELEFONO + CONTRASENA
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
            model.addAttribute("mensaje", "Error: Numero de telefono o contraseña incorrectos");
            return "login";
        }
    }

    // =============================================
    // GESTION DE CUENTAS (SOLO ADMINISTRADOR)
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

    // ✅ NUEVO: ACTUALIZAR DATOS DEL ADMINISTRADOR
    @PostMapping("/cuentas/actualizar")
    public String actualizarCuenta(
            @RequestParam Integer idUsuario,
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String contrasena,
            HttpSession sesion,
            RedirectAttributes ra) {

        // 1. Verificar que sea administrador
        String rol = (String) sesion.getAttribute("rolUsuario");
        if (rol == null || !rol.equals("ADMINISTRADOR")) {
            ra.addFlashAttribute("error", "Acceso exclusivo para Administradores");
            return "redirect:/dashboard";
        }

        try {
            // 2. Limpiar teléfono: solo números
            if (telefono != null && !telefono.isBlank()) {
                telefono = telefono.replaceAll("[^0-9]", "");
            } else {
                telefono = null;
            }

            // 3. Validar contraseña si se envió (solo números, mínimo 6)
            if (contrasena != null && !contrasena.isBlank()) {
                if (!contrasena.matches("[0-9]+")) {
                    ra.addFlashAttribute("error", "❌ Error: La contraseña solo puede contener números (0-9).");
                    return "redirect:/cuentas";
                }
                if (contrasena.length() < 6) {
                    ra.addFlashAttribute("error", "❌ Error: La contraseña debe tener mínimo 6 números.");
                    return "redirect:/cuentas";
                }
            } else {
                contrasena = null; // No se cambia la contraseña
            }

            // 4. Actualizar el usuario
            usuarioService.actualizarUsuario(idUsuario, nombre.trim(), correo.trim(), telefono, contrasena);

            // 5. Si se editó a sí mismo, actualizar la sesión
            Integer miId = (Integer) sesion.getAttribute("idUsuario");
            if (idUsuario.equals(miId)) {
                sesion.setAttribute("nombreUsuario", nombre.trim());
            }

            ra.addFlashAttribute("exito", "✅ Datos del administrador actualizados correctamente");
            return "redirect:/cuentas";

        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "❌ Error: " + e.getMessage());
            return "redirect:/cuentas";
        }
    }
}