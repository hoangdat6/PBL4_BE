package org.pbl4.pbl4_be.services;

import lombok.Getter;
import org.pbl4.pbl4_be.models.GameConfig;
import org.pbl4.pbl4_be.models.Room;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameRoomManager {
    public static GameRoomManager instance = new GameRoomManager();

    @Getter
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<Long, String> playerRoomMap = new ConcurrentHashMap<>();

    public static GameRoomManager getInstance() {
        if(instance == null) {
            instance = new GameRoomManager();
        }
        return instance;
    }

    /*
     * Khi tạo room, tạo luôn game đầu tiên cho room đó
     */
    public Room createRoom(String roomId, GameConfig gameConfig, boolean isPlayOnline) {
        Room room = new Room(roomId, gameConfig, isPlayOnline);
        rooms.put(roomId, room);
        room.addGame();
        return room;
    }

    private boolean isRoomExist(String roomId) {
        return rooms.containsKey(roomId);
    }

    public boolean checkRoomExist(String roomId) {
        return isRoomExist(roomId);
    }

    public void addPlayerToRoom(Long playerId, String roomId) {
        playerRoomMap.put(playerId, roomId);
    }

    public void removePlayerFromRoom(Long playerId) {
        playerRoomMap.remove(playerId);
    }

    public void removePlayerFromRoom(String roomId) {
        playerRoomMap.entrySet().removeIf(entry -> entry.getValue().equals(roomId));
    }

    public String getRoomCodeByPlayerId(Long playerId) {
//        for (Room room : rooms.values()) {
//            if (!room.isEnd()
//                    && room.checkPlayerExist(playerId)) {
//                return room.getRoomCode();
//            }
//        }
//        return null;

        return playerRoomMap.get(playerId);
    }

    public String getRoomCodeBySpectatorId(Long spectatorId) {
        for (Room room : rooms.values()) {
            if ( !room.isEnd()
                    && room.checkSpectatorExist(spectatorId)) {
                return room.getRoomCode();
            }
        }

        return null;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void removeRoom(String roomId) {
        rooms.remove(roomId);
        GameRoomManager.getInstance().removePlayerFromRoom(roomId);
    }

    public Integer getRoomsSize() {
        return rooms.size();
    }

}
