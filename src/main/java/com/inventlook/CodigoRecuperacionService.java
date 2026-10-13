package com.inventlook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class CodigoRecuperacionService {

    private final CodigoRecuperacionRepository codigoRepo;
    private final UsuarioRepository usuarioRepo; // ← necesitamos tu repositorio de usuarios

    public CodigoRecuperacionService(CodigoRecuperacionRepository codigoRepo, UsuarioRepository usuarioRepo) {
        this.codigoRepo = codigoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // Verifica que el correo exista y crea código nuevo (6 dígitos)
    @Transactional
    public String generarCodigoParaCorreo(String correo) {
        // 1. ¿Existe usuario con ese correo?
        if (!usuarioRepo.existsByCorreo(correo)) {
            return null; // no existe
        }

        // 2. Limpia códigos anteriores sin usar de este correo (opcional pero ordenado)
        Optional<CodigoRecuperacion> anterior = codigoRepo.findTopByCorreoAndUsadoFalseOrderByFechaExpiracionDesc(correo);
        anterior.ifPresent(codigo -> codigo.setUsado(true));

        // 3. Genera código numérico 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));

        // 4. Expira en 15 minutos
        CodigoRecuperacion nuevo = new CodigoRecuperacion(
                correo,
                codigo,
                LocalDateTime.now().plusMinutes(15)
        );
        codigoRepo.save(nuevo);

        return codigo;
    }

    //Valida código: mismo correo, no expirado, no usado
    @Transactional(readOnly = true)
    public Optional<CodigoRecuperacion> validarCodigo(String correo, String codigo) {
        Optional<CodigoRecuperacion> opt = codigoRepo.findTopByCorreoAndUsadoFalseOrderByFechaExpiracionDesc(correo);
        if (opt.isPresent()) {
            CodigoRecuperacion cr = opt.get();
            if (cr.getCodigo().equals(codigo) && !cr.estaExpirado()) {
                return Optional.of(cr);
            }
        }
        return Optional.empty();
    }

    // ✅ Marca código como ya utilizado
    @Transactional
    public void marcarComoUsado(CodigoRecuperacion cr) {
        cr.setUsado(true);
        codigoRepo.save(cr);
    }
}