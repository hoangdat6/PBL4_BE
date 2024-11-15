package org.pbl4.pbl4_be.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "first_player_id")
    private Long firstPlayerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ 1-nhiều với Move
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameMoveDB> moves;

    GameDB(Game game){
//        this.winnerId = game.getWinnerId();
//        this.startTime = game.getStartTime();
//        this.endTime = game.getEndTime();
//        this.firstPlayerId = game.getFirstPlayerId();
//        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }
}
