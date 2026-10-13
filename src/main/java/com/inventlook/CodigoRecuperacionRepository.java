package com.inventlook;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CodigoRecuperacionRepository extends JpaRepository<CodigoRecuperacion, Long> {

    //Buscar el código MÁS RECIENTE y NO USADO de un correo
    Optional<CodigoRecuperacion> findTopByCorreoAndUsadoFalseOrderByFechaExpiracionDesc(String correo);
}