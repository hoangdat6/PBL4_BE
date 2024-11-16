package org.pbl4.pbl4_be.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Move")
public class GameMoveDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ nhiều-1 với Game
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private GameDB game;

    private String move;

    private Integer duration;

    GameMoveDB(GameMove gameMove){
        this.duration = gameMove.getDuration();
        this.move = gameMove.getMove();

    }

    public GameMoveDB() {

    }
}
