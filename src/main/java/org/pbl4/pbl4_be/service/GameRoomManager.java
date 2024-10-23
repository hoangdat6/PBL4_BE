package org.pbl4.pbl4_be.service;

import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.model.Room;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameRoomManager {
    private Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom(String roomId, String ownerId, FirstMoveOption firstMoveOption) {
        Room room = new Room(roomId, ownerId, firstMoveOption);
        rooms.put(roomId, room);
        return room;
    }

    private boolean isRoomExist(String roomId) {
        return rooms.containsKey(roomId);
    }

    public boolean checkRoomExist(String roomId) {
        return isRoomExist(roomId);
    }

    public boolean checkPlayerExist(String roomId, String playerId) {
        if (isRoomExist(roomId)) {
            return rooms.get(roomId).checkPlayerExist(playerId);
        }
        return false;
    }

    public String getRoomCodeByPlayerId(String playerId) {
        for (Room room : rooms.values()) {
            if (room.checkPlayerExist(playerId)) {
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
}
