package org.pbl4.pbl4_be.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Entity
@Getter
@Setter
@Table(name = "player_season")
public class PlayerSeason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne

    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @ManyToOne
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    private Integer score;

    @NotNull
    @Column(name = "win_streak")
    private Integer winStreak;

    @NotNull
    @Column(name = "win_count")
    private Integer winCount;
    @NotNull
    @Column(name = "lose_count")
    private Integer loseCount;
    @NotNull
    @Column(name = "draw_count")
    private Integer drawCount;

    @Column(name = "player_time")
    private Integer playerTime;

    public PlayerSeason() {

    }

    @PrePersist
    public void OnCreate() {
        score = 0;
        winStreak = 0;
        winCount = 0;
        loseCount = 0;
        drawCount = 0;
        playerTime = 0;
    }

    public PlayerSeason(User player, Season season) {
        score = 0;
        winStreak = 0;
        winCount = 0;
        loseCount = 0;
        drawCount = 0;
        playerTime = 0;
        this.player = player;
        this.season = season;
    }

    public void updateScore(boolean isWin, boolean isDraw) {
        if (isWin) {
            score += 10;
            winStreak++;
            winCount++;
        } else if (isDraw) {
            score += 5;
            winStreak = 0;
            drawCount++;
        } else {
            score -= 5;
            winStreak = 0;
            loseCount++;
        }
        score = max(0, score);
        bonusScoreWinStreak();
    }

    public void bonusScoreWinStreak() {
        score += min(5, max(0, winStreak - 1));
    }

    public void bonusScoreTime(Integer myTime, Integer opponentTime) {
        int time = (myTime - opponentTime) / 60;
        score += min(5, max(0, time - 1));
    }

    public void increasePlayerTime(Integer time) {
        playerTime += time;
    }

}
