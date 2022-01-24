package com.codeoftheweb.salvo.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private ShipTypeEnum typeEnum;
    private String type;

    //incrustamos un objeto de tipo nativo
    @ElementCollection
    @Column(name = "positions")
    private List<String> locations;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Game_Player")
    private GamePlayer gamePlayer;

    public Ship() {
    }

    //constructor con enum
    public Ship(ShipTypeEnum type, GamePlayer gp, List<String> location) {
        this.typeEnum = type;
        this.locations = location;
        this.gamePlayer = gp;
    }

    //constructor sin enum
    public Ship(String type, GamePlayer gp, List<String> locations) {
        this.type = type;
        this.locations = locations;
        this.gamePlayer = gp;
    }


    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public String getGamePlayer() {
        return this.gamePlayer.getPlayer().getUserName();
    }

    public long getId() {
        return id;
    }

    public ShipTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations){this.locations = locations;}

    public String getType() {
        return type;
    }

    public Map<String, Object> shipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", this.getType());
        dto.put("locations", this.getLocations());
        return dto;

    }

}
