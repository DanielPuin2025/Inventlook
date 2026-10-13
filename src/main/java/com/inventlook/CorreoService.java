package com.inventlook;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    private final JavaMailSender mailSender;

    public CorreoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //Enviar código de recuperación por correo
    public void enviarCodigoRecuperacion(String correoDestino, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correoDestino);
        mensaje.setSubject("🔑 Código de recuperación de contraseña - InventLook");
        mensaje.setText("Hola!\n\n" +
                "Tu código de recuperación es: " + codigo + "\n\n" +
                "Este código es válido por 15 minutos.\n" +
                "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                "Saludos,\n" +
                "Equipo InventLook - Merca Max");
        mensaje.setFrom("noireliteofficial@gmail.com");

        mailSender.send(mensaje);
    }
}