package org.pbl4.pbl4_be.services;
import org.pbl4.pbl4_be.controllers.dto.GameDTO;
import org.pbl4.pbl4_be.controllers.dto.GameMoveDTO;
import org.pbl4.pbl4_be.models.RoomDB;
import org.pbl4.pbl4_be.controllers.dto.RoomDTO;
import org.pbl4.pbl4_be.repositories.RoomDBRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomDBService {
    public final RoomDBRepository roomDBRepository;

    public RoomDBService(RoomDBRepository roomDBRepository) {
        this.roomDBRepository = roomDBRepository;
    }

    public RoomDB save(RoomDB roomDB) {
        return roomDBRepository.save(roomDB);
    }

    public List<RoomDTO> getHistory(Long userId) {
        // Lấy tất cả các RoomDB nơi người dùng là player1 hoặc player2
        List<RoomDB> rooms = roomDBRepository.findAllByPlayer1IdOrPlayer2Id(userId, userId, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Chuyển danh sách RoomDB thành RoomDTO
        return rooms.stream().map(room -> {
            // Ánh xạ các GameDB thành GameDTO
            List<GameDTO> gameDTOs = room.getGames().stream()
                    .map(game -> {
                        // Ánh xạ danh sách GameMoveDB thành GameMoveDTO
                        List<GameMoveDTO> gameMoveDTOs = game.getMoves().stream()
                                .map(move -> new GameMoveDTO(move.getId(), move.getMove(), move.getDuration()))
                                .collect(Collectors.toList());

                        return new GameDTO(
                                game.getId(), game.getWinnerId(), game.getStartTime(), game.getEndTime(),
                                game.getFirstPlayerId(), game.getCreatedAt(), gameMoveDTOs);
                    })
                    .collect(Collectors.toList());

            // Trả về RoomDTO với các thông tin từ RoomDB và danh sách GameDTO
            return new RoomDTO(
                    room.getId(), room.getCode(), room.getPlayer1Id(), room.getPlayer2Id(),
                    room.getGameDuration(), room.getMoveDuration(), room.getIsPrivate(),
                    room.getStatus(), room.getCreatedBy(), room.getCreatedAt(), gameDTOs);
        }).collect(Collectors.toList());
    }

    public List<RoomDB> GetAllRoomByPlayerId(Long playerId) {
        return roomDBRepository.findAllByPlayer1IdOrPlayer2Id(playerId, playerId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public int countGamesByDate(String date) {
        return roomDBRepository.countGamesByDate(date);
    }

    public int countGamesByOnlineStatus(boolean isPlayOnline ,LocalDate startDate, LocalDate endDate) {
        return roomDBRepository.countGamesByOnlineStatus(isPlayOnline, startDate, endDate);
    }

    public int countDistinctPlayers(LocalDate startDate, LocalDate endDate) {
        return roomDBRepository.countDistinctPlayers(startDate, endDate);
    }


    @Transactional
    public RoomDB FindById(Long id) {
        return roomDBRepository.findById(id).orElse(null);
    }


}
