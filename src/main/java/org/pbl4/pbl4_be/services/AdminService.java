package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.controllers.dto.MatchDTO;
import org.pbl4.pbl4_be.controllers.dto.MatchData;
import org.pbl4.pbl4_be.controllers.dto.PlayerMatchDTO;
import org.pbl4.pbl4_be.controllers.dto.PlayerStatisticDTO;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.payload.response.PlayerStatisticResponse;
import org.pbl4.pbl4_be.payload.response.SeasonStatisticResponse;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AdminService {
    private final UserService userService;
    private final RoomDBService roomDBService;
    private final SeasonService seasonService;

    public AdminService(UserService userService, RoomDBService roomDBService, SeasonService seasonService) {
        this.userService = userService;
        this.roomDBService = roomDBService;
        this.seasonService = seasonService;
    }


    public PlayerStatisticResponse getAllPlayers(String sort, int page, int size) {
        int offset = page * size;
        int totalPage = userService.countTotalPlayers(size);
        List<Object[]> results = userService.getPlayerStatisticsWithPagination(sort, size, offset);
        return new PlayerStatisticResponse(totalPage, mapToPlayerStatisticDTO(results));
    }

    private List<PlayerStatisticDTO> mapToPlayerStatisticDTO(List<Object[]> results) {
        List<PlayerStatisticDTO> playerStatisticDTOS = new ArrayList<>();
        for (Object[] row : results) {
            Long userId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String email = (String) row[2];
            Timestamp createdAt = (Timestamp) row[3];
            int totalGames = ((Number) row[4]).intValue();
            int totalDraws = ((Number) row[5]).intValue();
            int totalWins = ((Number) row[6]).intValue();
            int totalLoses = ((Number) row[7]).intValue();
            playerStatisticDTOS.add(new PlayerStatisticDTO(userId, name, email, createdAt, totalGames, totalWins, totalLoses, totalDraws));
        }
        return playerStatisticDTOS;
    }


    public List<MatchDTO> getAllCurrentMatches() {
        List<MatchDTO> matchDTOS = new ArrayList<>();
        for(Room room: GameRoomManager.getInstance().getRooms().values()){
            PlayerMatchDTO player1 = new PlayerMatchDTO(room.getPlayers().get(0).getId(), room.getPlayers().get(0).getName(),
                    room.getPlayers().get(0).getAvatar(), room.getPlayers().get(0).getMatchScore());
            PlayerMatchDTO player2 = new PlayerMatchDTO(room.getPlayers().get(1).getId(), room.getPlayers().get(1).getName(),
                    room.getPlayers().get(1).getAvatar(), room.getPlayers().get(1).getMatchScore());
            matchDTOS.add(new MatchDTO(room.getRoomCode(), player1, player2));
        }
        return matchDTOS;
    }

    public List<String> getDatesBetween(ZonedDateTime startDate, ZonedDateTime endDate) {
        LocalDate startLocalDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        if (startLocalDate.isAfter(endLocalDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        long numOfDays = ChronoUnit.DAYS.between(startLocalDate, endLocalDate) + 1; // +1 để bao gồm cả endLocalDate
        return IntStream.range(0, (int) numOfDays)
                .mapToObj(startLocalDate::plusDays)
                .map(formatter::format) // Áp dụng định dạng cho từng ngày
                .collect(Collectors.toList());
    }

    public String formatDate(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);  // Chuyển đổi ngày thành chuỗi dd/MM/yyyy
    }

    public SeasonStatisticResponse getSeasonStatistic(Season season) {
        if (season == null) {
            return null;
        }
        List<MatchData> matchData = new ArrayList<>();
        for (String date : getDatesBetween(season.getStartDate(), season.getEndDate())) {
            matchData.add(new MatchData(date, roomDBService.countGamesByDate(date)));
        }

        LocalDate startDate = season.getStartDate().toLocalDate();
        LocalDate endDate = season.getEndDate().toLocalDate();

        int totalOnlineGames = roomDBService.countGamesByOnlineStatus(true, startDate, endDate);
        int totalFriendGames = roomDBService.countGamesByOnlineStatus(false, startDate, endDate);
        int totalPlayers = roomDBService.countDistinctPlayers(startDate, endDate);
        return new SeasonStatisticResponse(totalOnlineGames, totalFriendGames, totalPlayers, matchData);
    }


}
