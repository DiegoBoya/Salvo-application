package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static java.util.stream.Collectors.toList;

// en esta clase tenemos que poder representar el DLR de la foto de la task 2
@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    //la relacion GamePlayer con Player es M a 1, por es el ManyToOne va en GamePlayer
    //lo que esta en JoinCOlumn es el nombre que tendra esa columna
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_ID") //estl es el nombre de la columna
    private Player player;


    //la relacion GamePlayer con Game es M a 1, por es el ManyToOne va en GamePlayer
    //lo que esta en JoinCOlumn es el nombre que tendra esa columna
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_ID") // para la BD
    private Game game;

    //task3
    //un GAMEPLAYER tiene muchos SHIP
    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships;


    @OrderBy
    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes;


    //este lo usa el Bean
    public GamePlayer() {
        this.joinDate = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        //crea un set de ship vacio, seria como una hoja en blanco
        this.ships = new HashSet<>();
        this.salvoes = new HashSet<>();
    }

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        //crea un set de ship vacio, seria como una hoja en blanco
        this.ships = new HashSet<>();
        this.salvoes = new HashSet<>();
    }

    public long getId() {
        return this.id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }


    //esto es como un List, esta es la Coleccion Set, no es un setter.
    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    //estos son los datos de la partida en el juego, se devuelve un Json
    public Map<String, Object> gameViewDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("id", game.getId());
        dto.put("created", game.getDate());
        dto.put("gameState", "PLACESHIPS");
        //why not this.gamePlayerDTO() ???? quiere a los dos player del game, si hago lo otro solo me trae uno
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gp -> gp.gamePlayerDTO()).collect(toList()));
        dto.put("ships", ships.stream().map(sh -> sh.shipDTO()).collect(toList()));
        // pide los salvos de ambos player, entrar por game, el filter map devuelve los datos como los necesita el JSON
        dto.put("salvoes", game.getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(sa -> sa.salvoDTO())).collect(toList()));

        return dto;
    }


    public Map<String, Object> gamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", player.playerDTO());

        return dto;
    }

    public Optional<Score> getScore() {
        return player.getScore(this.game);
    }

    //se agrega un player para una objeto de gamePlayer, se agrega a un player al conjunto
    // de players que tiene el objeto de gamePlayers
    public void addShip(Ship ship) {
        ship.setGamePlayer(this); //define como gp a este GamePlayer, osea a si mismo, eso se resume poniendo "this"
        ships.add(ship);  // agraga al Set<Ship> de gamePlayers al objeto pasado por parametro
    }

    //task 8
    public void addSetOfShips(Set<Ship> ships) {
        ships.forEach(element -> { //a cada elemento del Set le aplica el metodo addShip(ship)
            addShip(element); //agega el ship pasado por parametro al gamePlayer
        });
    }

    //obtiene el oponente del GPlocal
    // el orElse crea un GamePlayer usando el constructor sin parametros. para que no rompa el hitsAndSinks(gpL, gpV)
    // al construcor vacio le agregamos una lista de salvo, date y una lista de ships, estas listas van a estar vacias, pero
    //ayuda a que no se obtenga un nullPointerException
    public GamePlayer getOpponent() {
        return this.getGame().getGamePlayers().stream().filter(
                gamePlayer -> gamePlayer.getId() != this.getId()).findFirst().orElse(new GamePlayer());
    }


}
