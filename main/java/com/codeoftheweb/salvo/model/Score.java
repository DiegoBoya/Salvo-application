package com.codeoftheweb.salvo.model;


import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.Player;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name= " game")
    private Game game;

    private float score;

    private LocalDateTime finishDate;

    public Score(){}

    public Score(Player player, Game game, float score, LocalDateTime d){
        this.player = player;
        this.game = game;
        this.score = score;
        this.finishDate =  d;
    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public Map<String, Object> scoreDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("score" , this.getScore());
        dto.put("player", this.getPlayer().getId());
        dto.put("finishDate", this.getFinishDate());
        return dto;
    }


}
