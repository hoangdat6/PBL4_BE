package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.controllers.dto.MatchDTO;
import org.pbl4.pbl4_be.controllers.dto.PlayerMatchDTO;
import org.pbl4.pbl4_be.controllers.dto.PlayerStatisticDTO;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.payload.response.PlayerStatisticResponse;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    private final UserService userService;

    public AdminService(UserService userService) {
        this.userService = userService;
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
            matchDTOS.add(new MatchDTO(room.getRoomId(), player1, player2));
        }
        return matchDTOS;
    }

}
