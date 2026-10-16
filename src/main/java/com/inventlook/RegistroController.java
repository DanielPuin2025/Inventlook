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
            @RequestParam String telefono, // ✅ OBLIGATORIO — todos deben tener teléfono
            RedirectAttributes redirectAttributes) {
        
        try {
            // ✅ Registrar con todas las validaciones: correo único, teléfono único, contraseña
            usuarioService.registrarUsuario(nombre, correo, contrasena, telefono);
            redirectAttributes.addFlashAttribute("exito", "✅ Usuario registrado correctamente");
            return "redirect:/registro"; // ✅ Se queda en el formulario para registrar otro
        } 
        catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
            return "redirect:/registro";
        }
    }
}