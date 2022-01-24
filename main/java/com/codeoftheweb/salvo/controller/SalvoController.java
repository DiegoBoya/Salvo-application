package com.codeoftheweb.salvo.controller;


import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import com.codeoftheweb.salvo.utilidades.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;


    @GetMapping("/gamesPlayers")
    public List<Object> getAllGamePlayers() {
        return gamePlayerRepository.findAll().stream().map(gp -> gp.gamePlayerDTO()).collect(toList());
    }

    @GetMapping("/players")
    public List<Object> getAllPlayers() {
        return playerRepository.findAll().stream().map(p -> p.playerDTO()).collect(toList());
    }


    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> makeShoot(@RequestBody Salvo salvo,
                                                         @PathVariable long gamePlayerId,
                                                         Authentication authentication) {
        ResponseEntity<Map<String, Object>> resp;
        Optional<GamePlayer> gpY = gamePlayerRepository.findById(gamePlayerId);
        //   Player pY = playerRepository.findByUserName(authentication.getName());
        Game game = gpY.get().getGame();

        if (Utilidades.isGuest(authentication)) {
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "you have to login"), HttpStatus.UNAUTHORIZED);
        } else if (!gpY.isPresent()) {
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "the gamePlayer is not in the repo"), HttpStatus.UNAUTHORIZED);
        } else if (game.getPlayers().size() < 2) {
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "you are alone, do you really want to shoot to the sea??"), HttpStatus.UNAUTHORIZED);
        } else if (gpY.get().getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()) {
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "you cant make shoots for other player"), HttpStatus.UNAUTHORIZED);
        } else if (salvo.getSalvoLocations().isEmpty()) {
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "select where do you want to shoot, cat!"), HttpStatus.FORBIDDEN);
        } else if (salvo.getSalvoLocations().size() > 5) {
            resp = new ResponseEntity<>(Utilidades.makeMap("error", "cant make more shoots than 5"), HttpStatus.FORBIDDEN);
        } else {
            GamePlayer gpVisitante = gpY.get().getOpponent();
            if (game.equalTurn(gpY, gpVisitante)) {
                salvo.setTurn(gpY.get().getSalvoes().size() + 1);
                salvo.setGamePlayer(gpY.get());
                salvoRepository.save(salvo);
                resp = new ResponseEntity<>(Utilidades.makeMap("OK", "shooting pra pra praaaaa !! DIEEE !!!"), HttpStatus.CREATED);
            } else {
                resp = new ResponseEntity<>(Utilidades.makeMap("error", "wait!!! your oponent did not made his shoots"), HttpStatus.EXPECTATION_FAILED);
            }
        }
        return resp;

    }


}



