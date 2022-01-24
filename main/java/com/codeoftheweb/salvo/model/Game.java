package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

// 1) primero definimos a la clase Game como una entidad
@Entity
public class Game {

    //2) segundo hacemos que la generacion del Id sea automatica
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime date;

    //constructor vacio para el @Bean
    public Game() {
        this.date = LocalDateTime.now();
    }

    //construcor usado
    public Game(LocalDateTime date) {
        this.date = date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }


    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();
    //este Set podria ser un arrayList o List, por eso va en plural, agrega u

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    //se agrega un game para una objeto de gamePlayer, se agrega a un game al conjunto
    // de players que tiene el objeto de gamePlayers
    public void addGamePlayer(GamePlayer gamePlayer) {
        //dice define como game de este GamePlayer a this, osea a si mismo
        gamePlayer.setGame(this);
        // agraga a la lista de gamePlayers al objeto pasado por parametro
        gamePlayers.add(gamePlayer);
    }

    // hayque ponerle el Json ignore para que no haga recursividad
    //este metodo le pide a un game todos sus players
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }


    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    public Set<Score> getScore() { return this.scores;    }

    //----------------------------------------------   DTO   -----------------------------------------------
    //este metodo va a mandar al json la info que yo permita, osea la info de esta clase
    public Map<String, Object> gameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("created", this.getDate());
        dto.put("id", this.getId());
        //puede que passen un front nuevo con "players"
        dto.put("gamePlayers", gamePlayers.stream().map(gp -> gp.gamePlayerDTO()).collect(toList()));

        List<Map<String, Object>> scores = gamePlayers.stream()
                .map(gp -> gp.getScore()).filter(score -> score.isPresent()).map(score -> score.get().scoreDTO())
                .collect(Collectors.toList());

        dto.put("scores", scores);


        return dto;
    }


    public boolean equalTurn(Optional<GamePlayer> gamePlayerLocal, GamePlayer gamePlayerVisitante) {
        //si es true, el player puede hacer un Salvo, sino no.
        return gamePlayerLocal.get().getSalvoes().size() <= gamePlayerVisitante.getSalvoes().size();
    }
}


