package com.inventlook;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String pantallaBienvenida() {
        return "index";
    }
}
