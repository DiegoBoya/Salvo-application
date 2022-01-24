package com.codeoftheweb.salvo.controller;


import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.utilidades.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;


    //PARA QUE APAREZCA EN LA VISTA EL USUARIO LOGUEADO
    //devuelve un Json con la info de los juegos actuales
    @GetMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> data = new LinkedHashMap<>();
        if (!Utilidades.isGuest(authentication)) {
            data.put("player", playerRepository.findByUserName(authentication.getName()).playerDTO());
        } else {
            data.put("player", "Guest");
        }
        data.put("games", gameRepository.findAll().stream().map(g -> g.gameDTO()).collect(toList()));
        return data;
    }

    //CREACION DE UN NUEVO JUEGO DESDE EL FRONT
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {

        ResponseEntity<Map<String, Object>> response;
        //si esto es true, tenemos un usuario
        if (!Utilidades.isGuest(authentication)) {
            Player playerX = playerRepository.findByUserName(authentication.getName());
            Game gameX = gameRepository.save(new Game());
            GamePlayer gp = gamePlayerRepository.save(new GamePlayer(playerX, gameX));
            response = new ResponseEntity<>(Utilidades.makeMap("gpid", gp.getId()), HttpStatus.CREATED);
        } else {
            // esto muestra guest si no hay logueado
            response = new ResponseEntity<>(Utilidades.makeMap("player", "Guest"), HttpStatus.UNAUTHORIZED);
        }
        return response;

    }

    // join game
    @PostMapping("/game/{gmid}/players")
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable long gmid) {
        ResponseEntity<Map<String, Object>> resp ;
        Optional<Game> gameX = gameRepository.findById(gmid);
        //si es huesped, es que no esta logueado
        if (Utilidades.isGuest(authentication)) {
            resp = new ResponseEntity<>(Utilidades.makeMap(" error", "you have to login"), HttpStatus.UNAUTHORIZED);
        } else if (!gameX.isPresent()) {
            // si no estaba presente, y lo niego, me da true => mensaje de juego no encontrado
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "that game doesent exist"), HttpStatus.FORBIDDEN);
        } else if (gameX.get().getPlayers().size() > 1) {
            //getPlayers devuelve una List<Player>, le puedo preguntar el size(), si es > 1 es que esta lleno
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "the Game is full"), HttpStatus.FORBIDDEN);
        } else {
            // si no cumplio niguna de las condiciones anteriores, esta todo OK
            Player playerX = playerRepository.findByUserName(authentication.getName());
            GamePlayer gpX = gamePlayerRepository.save(new GamePlayer(playerX, gameX.get()));
            resp = new ResponseEntity<>(Utilidades.makeMap("gpid", gpX.getId()), HttpStatus.CREATED);
        }
        return resp;
    }

}
