package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.utilidades.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/api")
public class ShipController {

    @Autowired
    private PlayerRepository playerRepository;


    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    // agrega barcos a mi partida, debe coincidir el Id del player logueado con el Id del player que tiene el
    //gamePlayerId pasado por parametro
    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable long gamePlayerId,
                                                        @RequestBody Set<Ship> ships,
                                                        Authentication authentication) {
        ResponseEntity<Map<String, Object>> resp;
        Player pX = playerRepository.findByUserName(authentication.getName());

        Optional<GamePlayer> gpX = gamePlayerRepository.findById(gamePlayerId);
        //si es huesped, es que no esta logueado
        if (Utilidades.isGuest(authentication)) {
            resp = new ResponseEntity<>(Utilidades.makeMap(" error", "you have to login"), HttpStatus.UNAUTHORIZED);
            //existe ese gamePlayer pasado por parametro??
        } else if (!gpX.isPresent()) {
            // si no estaba presente, y lo niego, me da true => mensaje de gp no encontrado
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "that gamePlayer doesent exist"), HttpStatus.UNAUTHORIZED);
            //si el id del player logueado es difetente al Id del player que esta en el gp
            // pasado en la URL, no puede agregar barcos, si no ponemos esto, le agregaria barcos a otro player
        } else if (gpX.get().getPlayer().getId() != pX.getId()) {
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "you are not in this gamePlayer"), HttpStatus.UNAUTHORIZED);
        } else if (gpX.get().getShips().size() > 0) {
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "you already have ships"), HttpStatus.FORBIDDEN);
        } else if (ships.isEmpty()) {
            // si manda un set vacio
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "you have not sent any ship"), HttpStatus.UNAUTHORIZED);
        } else {
            // si no cumplio niguna de las condiciones anteriores, esta todo OK
            // 1- agrego los ships a ese gamePlayer
            gpX.get().addSetOfShips(ships);
            // 2- actualizo lo que hay en el repo de gamePlayer, asi la BD se actualiza
            gamePlayerRepository.save(gpX.get());
            // 3- envio el status
            resp = new ResponseEntity<>(Utilidades.makeMap("OK", " ships are positioned"), HttpStatus.CREATED);
        }
        return resp;


    }






}
