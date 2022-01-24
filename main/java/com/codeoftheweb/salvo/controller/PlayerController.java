package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.utilidades.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;


    @Autowired
    PasswordEncoder passwordEncoder;

    // boton SING UP
    // crear NUEVOS USUARIOS con el boton sing up - recibe por parametro lo que se envia en el front
    // me permite verificar si mi registro de usuario cumple con algunas condiciones. Si cumple guarda
    // al player nuevo y lo crea
    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> register(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(Utilidades.makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(email) != null) {
            return new ResponseEntity<>(Utilidades.makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));

        return new ResponseEntity<>(Utilidades.makeMap("messege", "succes, player created"), HttpStatus.CREATED);
    }
}
