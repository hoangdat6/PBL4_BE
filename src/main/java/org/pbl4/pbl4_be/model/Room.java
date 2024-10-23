package org.pbl4.pbl4_be.model;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.enums.RoomStatusTypes;

import java.util.ArrayList;
import java.util.List;

import static org.pbl4.pbl4_be.Constants.MAX_PLAYER;

@Getter
@Setter
public class Room {
    private String roomId;
    private String roomCode;
    private String ownerId;
    private final List<Player> players;
    private final List<Player> spectators;
    private List<Game> games;
    private int nextGameId;
    private RoomStatusTypes roomStatusTypes;
    private FirstMoveOption firstMoveOption;

    public Room(String roomCode, String ownerId, FirstMoveOption firstMoveOption) {
        this.roomCode = roomCode;
        this.ownerId = ownerId;
        this.games = new ArrayList<>();
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.nextGameId = 1;
        this.roomStatusTypes = RoomStatusTypes.GAME_NOT_STARTED;
        this.firstMoveOption = firstMoveOption;
    }

    public Game addGame() {
        Game newGame = new Game(roomCode, nextGameId, firstMove());
        games.add(newGame);
        nextGameId++;
        return newGame;
    }

    public String firstMove() {
        if (firstMoveOption == FirstMoveOption.RANDOM) {
            return Math.random() < 0.5 ? players.get(0).getPlayerId() : players.get(1).getPlayerId();
        }

        return firstMoveOption == FirstMoveOption.ROOM_OWNER ? players.get(0).getPlayerId() : players.get(1).getPlayerId();
    }


    public Game getGamePlaying() {
        return games.get(games.size() - 1);
    }

    public boolean checkPlayerExist(String playerId) {
        for (Player player : players) {
            if (player.getPlayerId().equals(playerId)) {
                return true;
            }
        }

        return false;
    }

    public Player getPlayer(String playerId) {
        for (Player player : players) {
            if (player.getPlayerId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    public boolean isFull() {
        return players.size() == MAX_PLAYER;
    }

    public boolean addPlayer(Player player) {
        if (!isFull()) {
            players.add(player);
            if(isFull()) {
                setRoomStatusTypes(RoomStatusTypes.GAME_STARTED);
            }
            return true;
        }
        return false;
    }

    public void addSpectator(Player player) {
        spectators.add(player);
    }

    public boolean checkSpectatorExist(String playerId) {
        for (Player player : spectators) {
            if (player.getPlayerId().equals(playerId)) {
                return true;
            }
        }

        return false;
    }

}
