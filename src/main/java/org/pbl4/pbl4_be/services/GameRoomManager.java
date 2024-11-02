package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.controller.dto.ConfigGameDTO;
import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.enums.RoomStatusTypes;
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


    public Room createRoom(String roomId, ConfigGameDTO configGameDTO) {
        Room room = new Room(roomId, configGameDTO);
        rooms.put(roomId, room);
        return room;
    }

    private boolean isRoomExist(String roomId) {
        return rooms.containsKey(roomId);
    }

    public boolean checkRoomExist(String roomId) {
        return isRoomExist(roomId);
    }

    public String getRoomCodeByPlayerId(String playerId) {
        for (Room room : rooms.values()) {
            if ( !room.isEnd()
                    && room.checkPlayerExist(playerId)) {
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