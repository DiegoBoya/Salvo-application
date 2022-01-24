package com.codeoftheweb.salvo.controller;


import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import com.codeoftheweb.salvo.utilidades.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ShipRepository shipRepository;


    // D+D
    @GetMapping("/game_view/{gpid}")
    public ResponseEntity<Map<String, Object>> getGameViewByGamePlayerID(@PathVariable long gpid, Authentication authentication) {
        if (Utilidades.isGuest(authentication)) {
            return new ResponseEntity<>(Utilidades.makeMap("error", "login and try again"), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gpid).orElse(null);

        //si es true, es que no esta logueado
        if (player == null) {
            return new ResponseEntity<>(Utilidades.makeMap("error", "login and try again"), HttpStatus.UNAUTHORIZED);
        }

        // pregunto esto porque pueden haber pasado por parametro cualquier id de gamePlayer
        if (gamePlayer == null) {
            return new ResponseEntity<>(Utilidades.makeMap("error", "that game player doesent exist"), HttpStatus.NO_CONTENT);
        }

        // si el ID del que esta autenticado, es igual al ID del player en el gp solicitado.. es que es el mismo player
        if (gamePlayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(Utilidades.makeMap("error", "Cant see information of other player"), HttpStatus.CONFLICT);
        }

        Map<String, Object> dto = new LinkedHashMap<>();
        Map<String, Object> hits = new LinkedHashMap<>();

        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getDate());
        dto.put("gameState", this.gameStatus(gamePlayer));

        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(gamePlayer1 -> gamePlayer1.gamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(ship -> ship.shipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.salvoDTO()))
                .collect(Collectors.toList()));
        hits.put("self", hitsAndSinks(gamePlayer, gamePlayer.getOpponent()));
        hits.put("opponent", hitsAndSinks(gamePlayer.getOpponent(), gamePlayer));
        dto.put("hits", hits);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    // aca generamos el JSON de los hits,. comparando el salvo del oponente con los barcos del local
    private List<Map> hitsAndSinks(GamePlayer self, GamePlayer opponent) {

        List<Map> hits = new ArrayList<>();

        // contadores: TOTAL DAMAGE - damage global del ship
        int carrierHits = 0;
        int battleshipHits = 0;
        int submarineHits = 0;
        int destroyerHits = 0;
        int patrolboatHits = 0;

        //locations
        // obtengo para cada barco su lista de locations
        List<String> carrierLocations = findShipLocations(self, "carrier");
        List<String> battleshipLocations = findShipLocations(self, "battleship");
        List<String> submarineLocations = findShipLocations(self, "submarine");
        List<String> destroyerLocations = findShipLocations(self, "destroyer");
        List<String> patrolboatLocations = findShipLocations(self, "patrolboat");

        //foreach de los salvo del oponente, para cada salvo voy a hacer todo lo que este abajo:
        for (Salvo salvo : opponent.getSalvoes()) {

            Map<String, Object> damagePerTurn = new LinkedHashMap<>();
            Map<String, Object> hitsPerTurn = new LinkedHashMap<>();
            ArrayList<String> hitCellList = new ArrayList<>();

            // empieza en el size() de los salvo, y en las iteraciones va descontando si entra en el if
            int missed = salvo.getSalvoLocations().size();

            //Hits per turn
            // al estar declarados dentro del for, en cada iteracion ( en cada turno) vuelve a cero
            int carrierTurn = 0;
            int battleshipTurn = 0;
            int submarineTurn = 0;
            int destroyerTurn = 0;
            int patrolboatTurn = 0;

            //para cada location del salvo, la comparo con cada ubicacion de mis ship
            for (String location : salvo.getSalvoLocations()) {
                if (carrierLocations.contains(location)) {
                    carrierHits++; //contador global del ship
                    carrierTurn++; //contador por turno
                    missed--; // si le dio algun salvo, descuenta de los tiros perdidos
                    hitCellList.add(location); // si le dio algun tiro, lo agrega en esta lista para mostrarlos luego|
                }
                if (battleshipLocations.contains(location)) {
                    battleshipHits++;
                    battleshipTurn++;
                    missed--;
                    hitCellList.add(location);
                }
                if (submarineLocations.contains(location)) {
                    submarineHits++;
                    submarineTurn++;
                    missed--;
                    hitCellList.add(location);
                }
                if (destroyerLocations.contains(location)) {
                    destroyerHits++;
                    destroyerTurn++;
                    missed--;
                    hitCellList.add(location);
                }
                if (patrolboatLocations.contains(location)) {
                    patrolboatHits++;
                    patrolboatTurn++;
                    missed--;
                    hitCellList.add(location);
                }
            }//cierre del segundo for
            //damage por turno
            damagePerTurn.put("carrierHits", carrierTurn);
            damagePerTurn.put("battleshipHits", battleshipTurn);
            damagePerTurn.put("submarineHits", submarineTurn);
            damagePerTurn.put("destroyerHits", destroyerTurn);
            damagePerTurn.put("patrolboatHits", patrolboatTurn);
            //total damage
            damagePerTurn.put("carrier", carrierHits);
            damagePerTurn.put("battleship", battleshipHits);
            damagePerTurn.put("submarine", submarineHits);
            damagePerTurn.put("destroyer", destroyerHits);
            damagePerTurn.put("patrolboat", patrolboatHits);
            //
            hitsPerTurn.put("turn", salvo.getTurn());
            hitsPerTurn.put("missed", missed);
            hitsPerTurn.put("damages", damagePerTurn);// agrega al map el map de los damages per turn
            hitsPerTurn.put("hitLocations", hitCellList); // agrega al map, la lista de tiros acertados
            //List
            hits.add(hitsPerTurn); // agrega a la lista de hits, los hits per turn
        }// cierre del primer for
        return hits;
    }


    private List<String> findShipLocations(GamePlayer gamePlayer, String type) {
        //filtra y devuelve los que sean true, de esos, agarra el primero. Y
        // cuando lo tiene, le obtiene la lista de locations y lo retorna

        Optional<Ship> s = gamePlayer.getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst();
        if (s.isPresent()) {
            return s.get().getLocations();
        } else {
            //para que tome los ships que envia el front
            return new ArrayList<>();
        }
    }


    //Metod task 11 = devuelve true si bajaron todos los barcos del que es self
    //si la lista collect de salvos contiene a todas las locations de los ships devuelve true!!
    //return true si le bajaron todas las naves al gp self
    private Boolean getIfAllSunk(GamePlayer self, GamePlayer opponent) {
        // if el oponente tiene ships y YO dispare salvos...
        if (!opponent.getShips().isEmpty() && !self.getSalvoes().isEmpty()) {
            return opponent.getSalvoes().stream().flatMap(salvo ->
                    salvo.getSalvoLocations().stream()).collect(Collectors.toList()).
                    containsAll(self.getShips().stream().flatMap(
                            ship -> ship.getLocations().stream()).collect(Collectors.toList()));
        }
        return false;
    }

    private String gameStatus(GamePlayer gpSelf) {
        float tie = 0.5f;
        float win = 1;
        float looser = 0;


        if (gpSelf.getShips().size() < 1)
            return "PLACESHIPS";
        // preguntar gpOpponent == null esta mal, xq el metodo getOpponent trae un gp si o si
        if (gpSelf.getGame().getGamePlayers().size() == 1)
            return "WAITINGFOROPP";

        GamePlayer gpOpponent = gpSelf.getOpponent();

        //si es true, el opp no hizo el placeShips
        if (gpOpponent.getShips().size() == 0)
            return "WAIT";

        //si esto es true, no puedo disparar, paso a estado wait, si es false, entra al else if
        if (gpSelf.getSalvoes().size() > gpOpponent.getSalvoes().size()) {
            return "WAIT";
            //si estan en el mismo turno, reviso si hay ganador
        } else if (gpSelf.getSalvoes().size() == gpOpponent.getSalvoes().size()) {

            // si es true, le hundieron todos los barcos
            boolean self = getIfAllSunk(gpSelf, gpOpponent);
            boolean opponent = getIfAllSunk(gpOpponent, gpSelf);

            //ambos player , all hundido
            if (self && opponent) {
                scoreRepository.save(new Score(gpSelf.getPlayer(), gpSelf.getGame(), tie, LocalDateTime.now()));
                return "TIE";
            }
            //el self todavia tiene barcos, pero el oponent no - self WIN
            if (!self && opponent) {
                scoreRepository.save(new Score(gpSelf.getPlayer(), gpSelf.getGame(), win, LocalDateTime.now()));
                return "WIN";
            }

            //el opponent todavia tiene barcos, pero el self no - self loose
            if (self && !opponent) {
                scoreRepository.save(new Score(gpSelf.getPlayer(), gpSelf.getGame(), looser, LocalDateTime.now()));
                return "LOST";
            }

            return "PLAY"; //SALE del else if  en estado PLAY, estan en el mismo turno, pero hay barcos a flote


        } else return "PLAY"; //no entra al else if, porque el turno es menor


    }
}





