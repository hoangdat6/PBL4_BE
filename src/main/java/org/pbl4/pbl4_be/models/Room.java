package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.enums.RoomStatusTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Map.Entry<String, String> firstMove() {
        if(games.isEmpty()) {
            if (firstMoveOption == FirstMoveOption.RANDOM) {
                return Math.random() < 0.5 ? getPlayers(0, 1) : getPlayers(1, 0);
            }

            return firstMoveOption == FirstMoveOption.ROOM_OWNER ? getPlayers(0, 1) : getPlayers(1, 0);
        }else {
            return games.get(games.size() - 1).getFirstPlayerId().equals(players.get(0).getPlayerId()) ?
                    getPlayers(1, 0) : getPlayers(0, 1);
        }
    }

    public Map.Entry<String, String> getPlayers(int first, int last) {
        return Map.entry(players.get(first).getPlayerId(), players.get(last).getPlayerId());
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
            if (isFull()) {
                setRoomStatusTypes(RoomStatusTypes.GAME_STARTED);
            }
        }
        return checkFull();
    }

    public boolean checkFull() {
        return players.size() == MAX_PLAYER;
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

    public void removePlayer(String playerId) {
        players.removeIf(player -> player.getPlayerId().equals(playerId));
    }

    public void removeSpectator(String playerId) {
        spectators.removeIf(player -> player.getPlayerId().equals(playerId));
    }

    public void startGame() {
        this.addGame();
    }
}
