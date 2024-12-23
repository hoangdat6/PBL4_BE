package org.pbl4.pbl4_be.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Rooms")
public class RoomDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String password;

    @Column(name = "player1_id")
    private Long player1Id;

    @Column(name = "player2_id")
    private Long player2Id;

    @Column(name = "game_duration")
    private Integer gameDuration;

    @Column(name = "move_duration")
    private Integer moveDuration;

    @Column(name = "is_private")
    private Boolean isPrivate;

    private String status;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GameDB> games;

    public RoomDB(Room room){
        this.code = room.getRoomCode();
        this.password = room.getPassword();
        this.player1Id = room.getPlayers().get(0).getId();
        this.player2Id = room.getPlayers().get(1).getId();
        this.gameDuration = room.getGameConfig().getMoveDuration();
        this.moveDuration = room.getGameConfig().getTotalTime();
        this.isPrivate = room.isPrivate();
        this.status = room.getRoomStatusTypes().toString();
        this.createdBy = room.getPlayers().get(0).getId();
        this.createdAt = LocalDateTime.now();
    }

    public void addGame(Game game){
        GameDB gameDB = new GameDB(game);
        gameDB.setRoom(this);
        this.games.add(gameDB);
    }

    public RoomDB() {

    }

}