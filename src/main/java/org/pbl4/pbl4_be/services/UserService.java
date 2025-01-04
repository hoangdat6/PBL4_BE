package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.controllers.dto.ProfileDTO;
import org.pbl4.pbl4_be.controllers.dto.UserDTO;
import org.pbl4.pbl4_be.models.PlayerSeason;
import org.pbl4.pbl4_be.models.Season;
import org.pbl4.pbl4_be.models.User;
import org.pbl4.pbl4_be.repositories.PlayerSeasonRepository;
import org.pbl4.pbl4_be.repositories.SeasonRepository;
import org.pbl4.pbl4_be.repositories.UserRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PlayerSeasonRepository playerSeasonRepository;
    private final SeasonRepository seasonRepository;
    private final PlayerSeasonService playerSeasonService;

//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PlayerSeasonRepository playerSeasonRepository, SeasonRepository seasonRepository, PlayerSeasonService playerSeasonService) {
        this.userRepository = userRepository;
        this.playerSeasonRepository = playerSeasonRepository;
        this.seasonRepository = seasonRepository;
        this.playerSeasonService = playerSeasonService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public Optional<UserDTO> findUserWithScoreBySeasonId(Long userId, Integer score) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(new UserDTO(user.getId(), user.getName(), user.getAvatar(), score));
    }


    /**
     * Find profile by user id
     * @param userId user id
     * @return profile dto
     */
    public Optional<ProfileDTO> findProfileById(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        /**
            * Get current season
            * Get player season by season id and player id
         */
        Season currentSeason = seasonRepository.findCurrentSeason().orElse(null);

        /**
         * Create profile dto
         */
        ProfileDTO profileDTO = ProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())

                .dateJoined(
                        user.getCreatedAt().getDayOfMonth() + "/" +
                                user.getCreatedAt().getMonthValue() + "/" +
                                user.getCreatedAt().getYear()
                )
                .build();

        PlayerSeason playerSeason = playerSeasonRepository.findBySeasonIdAndPlayerId(currentSeason.getId(), userId).orElse(null);

        if (playerSeason != null) {
            profileDTO.setRank(playerSeasonService.getRank(playerSeason.getScore(), currentSeason.getId()));
            profileDTO.setPoints(playerSeason.getScore());
            profileDTO.setWins(playerSeason.getWinCount());
            profileDTO.setDraws(playerSeason.getDrawCount());
            profileDTO.setLosses(playerSeason.getLoseCount());
            profileDTO.setStreaks(playerSeason.getWinStreak());
            profileDTO.setPlayTimes(intToTime(playerSeason.getPlayerTime()));
        } else {
            profileDTO.setPoints(0);
            profileDTO.setWins(0);
            profileDTO.setDraws(0);
            profileDTO.setLosses(0);
            profileDTO.setStreaks(0);
            profileDTO.setPlayTimes("0h 0m");
        }

        return Optional.of(profileDTO);
    }

    /**
     * Convert integer to time format
     * @param time in minutes
     * @return time in format "h m"
     */
    public String intToTime(int time) {
        int hours = time / 60;
        int minutes = time % 60;
        return hours + "h " + minutes + "m";
    }

    public List<Object[]> getPlayerStatisticsWithPagination(String sortColumn, int size, int offset) {
        return userRepository.getPlayerStatisticsWithPagination(sortColumn, size, offset);
    }

    public int countTotalPlayers(int size) {
        return (userRepository.countTotalPlayers() + size - 1) / size;
    }
}