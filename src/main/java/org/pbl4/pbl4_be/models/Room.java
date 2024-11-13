package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.controllers.dto.ConfigGameDTO;
import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.enums.GameStatus;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.pbl4.pbl4_be.Constants.MAX_PLAYER;

@Getter
@Setter
public class Room {
    private String roomId;
    private String roomCode;
    private ConfigGameDTO configGameDTO;
    private final List<Player> players;
    private final List<Player> spectators;
    private List<Game> games;
    private int nextGameId;
    private GameStatus roomStatusTypes;

    public Room(String roomCode, ConfigGameDTO configGameDTO) {
        this.roomCode = roomCode;
        this.configGameDTO = configGameDTO;
        this.games = new ArrayList<>();
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.nextGameId = 1;
        this.roomStatusTypes = GameStatus.NOT_STARTED;
    }

    public Game addGame() {
        Game newGame = new Game(roomCode, nextGameId, firstMove());
        games.add(newGame);
        nextGameId++;
        return newGame;
    }

    public boolean isEnd() {
        return roomStatusTypes == GameStatus.ENDED;
    }

    public Map.Entry<String, String> firstMove() {
        if(games.isEmpty()) {
            if (configGameDTO.getFirstMoveOption() == FirstMoveOption.RANDOM) {
                return Math.random() < 0.5 ? getPlayers(0, 1) : getPlayers(1, 0);
            }

            return configGameDTO.getFirstMoveOption() == FirstMoveOption.ROOM_OWNER ? getPlayers(0, 1) : getPlayers(1, 0);
        }else {
            return games.get(games.size() - 1).getFirstPlayerId().equals(players.get(0).getPlayerId()) ?
                    getPlayers(1, 0) : getPlayers(0, 1);
        }
    }

    public Map.Entry<String, String> getPlayers(int first, int last) {
        return Map.entry(players.get(first).getPlayerId(), players.get(last).getPlayerId());
    }


    public Game getGamePlaying() {
        if(games.isEmpty()) {
            return null;
        }
        Game game = games.get(games.size() - 1);
        return game.isEnd() ? null : game;
    }

    public Game getLastGame() {
        if(games.isEmpty()) {
            return null;
        }
        return games.get(games.size() - 1);
    }

    public boolean checkPlayerExist(String playerId) {
        for (Player player : players) {
            if (player.getPlayerId().equals(playerId) && !player.isLeaveRoom()) {
                return true;
            }
        }

        return false;
    }


    public boolean isFull() {
        return players.size() == MAX_PLAYER;
    }

    public void addPlayer(Player player) {
        if(isFull()) {
            return;
        }
        players.add(player);
        if (isFull()) {
            setRoomStatusTypes(GameStatus.STARTED);
        }
    }

    public boolean checkFull() {
        for (Player player : players) {
            if (player.isLeaveRoom()) {
                return false;
            }
        }

        return players.size() == MAX_PLAYER;
    }

    public void addSpectator(Player player) {
        spectators.add(player);
    }

    public boolean checkSpectatorExist(String playerId) {
        for (Player player : spectators) {
            if (player.getPlayerId().equals(playerId) && !player.isLeaveRoom()) {
                return true;
            }
        }

        return false;
    }

    public void removePlayer(String playerId) {
        players.forEach(player -> {
            if(player.getPlayerId().equals(playerId)) {
                player.setLeaveRoom(true);
            }
        } );

        if(players.isEmpty() && roomStatusTypes == GameStatus.STARTED) {
            setRoomStatusTypes(GameStatus.ENDED);
        }
    }

    public void removeSpectator(String playerId) {
        spectators.forEach(player -> {
            if(player.getPlayerId().equals(playerId)) {
                player.setLeaveRoom(true);
            }
        });
    }

    public void startGame() {
        this.addGame();
    }

    public void setEnd() {
        this.roomStatusTypes = GameStatus.ENDED;

    }

}
