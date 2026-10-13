package com.inventlook;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrarFormularioRegistro() {
        return "registro";
    }

    @PostMapping
    public String registrarUsuarioNuevo(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes) {

        try {
            // ✅ VALIDACIÓN 1: Contraseña SOLO números
            if (contrasena == null || contrasena.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "❌ Error: La contraseña es obligatoria.");
                return "redirect:/registro";
            }

            if (!contrasena.matches("[0-9]+")) {
                redirectAttributes.addFlashAttribute("error", "❌ Error: La contraseña solo puede contener números (0-9), sin letras, puntos ni símbolos.");
                return "redirect:/registro";
            }

            // ✅ VALIDACIÓN 2: Contraseña mínimo 6 dígitos
            if (contrasena.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "❌ Error: La contraseña debe tener mínimo 6 números.");
                return "redirect:/registro";
            }

            // ✅ VALIDACIÓN 3: Teléfono SOLO números (si se envió)
            if (telefono != null && !telefono.isBlank()) {
                if (!telefono.matches("[0-9]+")) {
                    redirectAttributes.addFlashAttribute("error", "❌ Error: El teléfono solo puede contener números (0-9).");
                    return "redirect:/registro";
                }
            }

            // ✅ VALIDACIÓN 4: Nombre no vacío
            if (nombre == null || nombre.trim().isBlank()) {
                redirectAttributes.addFlashAttribute("error", "❌ Error: El nombre es obligatorio.");
                return "redirect:/registro";
            }

            // ✅ Si todo está bien, registramos el usuario
            if (telefono != null) {
            telefono = telefono.replaceAll("[^0-9]", "");
        }

            usuarioService.registrarUsuario(nombre.trim(), correo.trim(), contrasena, telefono);
            redirectAttributes.addFlashAttribute("exito", "✅ Usuario registrado correctamente");
            return "redirect:/login";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error: " + e.getMessage());
            return "redirect:/registro";
        }
    }
}