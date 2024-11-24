package org.pbl4.pbl4_be.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
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

    @NotNull
    private Integer score;





}
