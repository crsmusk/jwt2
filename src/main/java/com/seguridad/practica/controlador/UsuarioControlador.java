package com.seguridad.practica.controlador;

import org.springframework.web.bind.annotation.RestController;
import com.seguridad.practica.entidades.dtos.AuthResponse;
import com.seguridad.practica.entidades.dtos.iniciarSesion;
import com.seguridad.practica.entidades.dtos.registrarUsuario;
import com.seguridad.practica.servicio.details.UserDetailService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.security.AuthProvider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping("/usuarios")
public class UsuarioControlador {
    
    private final UserDetailService userDetailService;

    public UsuarioControlador(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @PostMapping("/registra")
    public AuthResponse register(@RequestBody registrarUsuario request) {
       return userDetailService.createUser(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody iniciarSesion request) {
        return userDetailService.loginUser(request);
    }
    
    @GetMapping("/saludo")
    public String getMethodName() {
        return "Hola, usuario!";
    }
    
    

}
