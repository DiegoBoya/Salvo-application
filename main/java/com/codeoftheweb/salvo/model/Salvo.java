package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    //incrustamos un objeto de tipo nativo
    @ElementCollection
    @Column(name = "salvo_locations")
    private List<String> salvoLocations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer")
    private GamePlayer gamePlayer;

    private int turn;

    public Salvo() {
        this.turn = 0;
        this.salvoLocations = new ArrayList<>();
    }

    public Salvo(GamePlayer gp, int turn, List<String> locations) {
        this.gamePlayer = gp;
        this.turn = turn;
        this.salvoLocations = locations;

    }


    public Long getId() {
        return id;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public int getTurn() {
        return turn;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int actualizeTurn() {
        return getTurn() + 1;
    }

    public Map<String, Object> salvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurn());
        //ahora cada salvo esta distinguido por el ID del player que le corresponde
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.getSalvoLocations());
        return dto;
    }
}
