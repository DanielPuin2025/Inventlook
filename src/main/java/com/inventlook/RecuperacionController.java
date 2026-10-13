package com.inventlook;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
public class RecuperacionController {

    private final CodigoRecuperacionService codigoService;
    private final CorreoService correoService;
    private final UsuarioRepository usuarioRepo;

    public RecuperacionController(CodigoRecuperacionService codigoService,
                                   CorreoService correoService,
                                   UsuarioRepository usuarioRepo) {
        this.codigoService = codigoService;
        this.correoService = correoService;
        this.usuarioRepo = usuarioRepo;
    }

    // 📄 Mostrar formulario para ingresar correo
    @GetMapping("/recuperar-contrasena")
    public String formRecuperar() {
        return "recuperar-contrasena";
    }

    // 📤 Enviar código al correo
    @PostMapping("/enviar-codigo-recuperacion")
    public String enviarCodigo(@RequestParam String correo,
                                RedirectAttributes ra) {
        String codigo = codigoService.generarCodigoParaCorreo(correo);
        if (codigo == null) {
            ra.addFlashAttribute("error", "⚠️ No existe ninguna cuenta con ese correo.");
            return "redirect:/recuperar-contrasena";
        }
        correoService.enviarCodigoRecuperacion(correo, codigo);
        ra.addFlashAttribute("exito", "✅ Revisa tu correo. Te enviamos el código de 6 dígitos.");
        return "redirect:/restablecer?correo=" + correo;
    }

    // 📄 Formulario para escribir código y nueva contraseña
    @GetMapping("/restablecer")
    public String formRestablecer(@RequestParam String correo, Model model) {
        model.addAttribute("correo", correo);
        return "restablecer-contrasena";
    }

    // 🔐 Validar código y GUARDAR nueva contraseña
    @PostMapping("/guardar-nueva-contrasena")
    public String cambiarClave(@RequestParam String correo,
                                @RequestParam String codigo,
                                @RequestParam String nuevaContrasena,
                                @RequestParam String confirmarContrasena,
                                Model model,
                                RedirectAttributes ra) {

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            model.addAttribute("correo", correo);
            model.addAttribute("error", "❌ Las contraseñas no coinciden.");
            return "restablecer-contrasena";
        }

        Optional<CodigoRecuperacion> valido = codigoService.validarCodigo(correo, codigo);
        if (valido.isEmpty()) {
            model.addAttribute("correo", correo);
            model.addAttribute("error", "❌ Código inválido, expiró o ya fue usado.");
            return "restablecer-contrasena";
        }

        // ✅ GUARDAR contraseña SIN Ñ → coincide con Usuario.java y MySQL
        Usuario u = usuarioRepo.findByCorreo(correo).orElseThrow();
        u.setContrasena(nuevaContrasena);
        usuarioRepo.save(u);

        codigoService.marcarComoUsado(valido.get());
        ra.addFlashAttribute("exito", "🎉 Tu contraseña ha sido actualizada. ¡Inicia sesión!");
        return "redirect:/login";
    }
}