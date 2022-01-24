package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static com.jayway.jsonpath.Filter.filter;
import static java.util.stream.Collectors.toList;

//@entity hace que la clase player se conecte a la Base de datos
@Entity
public class Player {

    // estas son anotaciones JPA, no llevan ";" al final.
    //@Id genera la primari key pra cada objeto, cada objeto sera una fila de la tabla de la BD
    // La tabla sera la clase PlayerRepository, que guarda a todos los players
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;

    private String password;

    public Player(){}

    public Player(String nombreUsuario, String pass){
        this.userName = nombreUsuario;
        this.password = pass;
        //setUserName(userName); lo que funcion no se toca
    }

    //el EAGER  es para la BD, sirve para que el player traiga sus relaciones en la consulta SQL
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();
    //este Set podria ser un arrayList o List, por eso va en plural, agrega u


    //esto es como un List, esta es la Coleccion Set, no es un setter.
    public Set<GamePlayer> getGamePlayer() {
        return gamePlayers;
    }

    //se agrega un player para una objeto de gamePlayer, se agrega a un player al conjunto
    // de players que tiene el objeto de gamePlayers
    public void addGamePlayer (GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this); //dice define como jugador de este GamePlayer a this, osea a si mismo
        gamePlayers.add(gamePlayer);  // agraga a la lista de gamePlayers al objeto pasado por parametro
    }

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    public Set<Score> getScore(){
        return this.scores;
    }


    public String getUserName() {
        return userName;
    }

    public long getId() {   return this.id;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        //podria hacerse una validacion aca, que el nombre usuario tenga un @ y un .com
        this.userName = userName;
    }

    //este metodo le pide a un player todos sus games
    //JsonIgnore, hace que Springboot no tome este metodo para mostrarlo, osea, en localHost8080/players cuando llegue al
    //metodo getGames, no lo va a mostrar
    @JsonIgnore
    public List<Game> getGames() {
    // devuelve a todos los game de este player
        return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList());
    }


    public Map<String, Object> playerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());

        return  dto;
    }


    //
    public Optional<Score> getScore(Game game){
        return scores.stream().filter(sc -> sc.getGame().getId() == game.getId()).findFirst();
    }




}
