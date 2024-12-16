package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.models.GameConfig;
import org.pbl4.pbl4_be.models.Room;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameRoomManager {
    public static GameRoomManager instance = new GameRoomManager();

    private Map<String, Room> rooms = new ConcurrentHashMap<>();

    public static GameRoomManager getInstance() {
        if(instance == null) {
            instance = new GameRoomManager();
        }
        return instance;
    }

    /*
     * Khi tạo room, tạo luôn game đầu tiên cho room đó
     */

    public Room createRoom(String roomId, GameConfig gameConfig) {
        Room room = new Room(roomId, gameConfig);
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

    public String getRoomCodeByPlayerId(Long playerId) {
        for (Room room : rooms.values()) {
            if ( !room.isEnd()
                    && room.checkPlayerExist(playerId)) {
                return room.getRoomCode();
            }
        }

        return null;
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
    }

    public Integer getRoomsSize() {
        return rooms.size();
    }
}
