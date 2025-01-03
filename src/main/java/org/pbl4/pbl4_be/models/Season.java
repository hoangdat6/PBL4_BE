package org.pbl4.pbl4_be.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "season")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name="name")
    private String name;

//    @NotBlank
    @Column(name="start_date")
    private ZonedDateTime startDate;

//    @NotBlank
    @Column(name="end_date")
    private ZonedDateTime endDate;

//    @NotBlank
    @Column(name="reward")
    private String reward;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlayerSeason> playerSeasons;

}
