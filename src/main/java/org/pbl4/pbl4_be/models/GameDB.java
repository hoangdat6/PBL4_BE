package org.pbl4.pbl4_be.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Entity
@Getter
@Setter
@Table(name = "Games")
public class GameDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ nhiều-1 với Room
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private RoomDB room;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "start_time")
    private ZonedDateTime startTime;

    @Column(name = "end_time")
    private ZonedDateTime endTime;

    @Column(name = "first_player_id")
    private Long firstPlayerId;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    // Quan hệ 1-nhiều với Move
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GameMoveDB> moves;

    public GameDB(Game game){
        this.winnerId = game.getWinnerId();
        this.startTime = game.getStartTime();
        this.endTime = game.getEndTime();
        this.firstPlayerId = game.getFirstPlayerId();
        this.createdAt = game.getCreatedAt();
        this.moves = new ArrayList<>();
        for(GameMove move : game.getMoveList()){
            GameMoveDB gameMoveDB = new GameMoveDB(move);
            gameMoveDB.setGame(this);
            this.moves.add(gameMoveDB);
        }
    }

    public GameDB() {

    }
}
