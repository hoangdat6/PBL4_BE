package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.enums.GameStatus;
import org.pbl4.pbl4_be.services.GameRoomManager;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.pbl4.pbl4_be.Constants.MAX_PLAYER;

@Getter
@Setter
public class Room {
    private Long roomId;
    private String roomCode;
    private GameConfig gameConfig;
    private final List<Player> players;
    private final List<Player> spectators;
    private List<Game> games;
    private int nextGameId;
    private GameStatus roomStatusTypes;
    private String password;
    private boolean isPrivate;
    private List<Message> messages;
    private boolean isPlayOnline;

    public Room(String roomCode, GameConfig gameConfig, boolean isPlayOnline) {
        this.roomCode = roomCode;
        this.gameConfig = gameConfig;
        this.games = new ArrayList<>();
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.nextGameId = 1;
        this.roomStatusTypes = GameStatus.NOT_STARTED;
        this.messages = new ArrayList<>();
        this.isPlayOnline = isPlayOnline;
    }

    public Game addGame() {
        Game newGame = new Game(roomCode, nextGameId, gameConfig);
        games.add(newGame);
        nextGameId++;
        return newGame;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean isEnd() {
        return roomStatusTypes == GameStatus.ENDED;
    }

    public Map.Entry<Long, Long> firstMove() {
        if(games.size() == 1) {
            if (gameConfig.getFirstMoveOption() == FirstMoveOption.RANDOM) {
                return Math.random() < 0.5 ? getPlayers(0, 1) : getPlayers(1, 0);
            } else {
                if (gameConfig.getFirstMoveOption() == FirstMoveOption.ROOM_OWNER) {
                    return getPlayers(0, 1);
                } else {
                    return getPlayers(1, 0);
                }
            }
        }else {
            return getGamePrevious().getFirstPlayerId().equals(players.get(0).getId()) ?
                    getPlayers(1, 0) : getPlayers(0, 1);
        }

    }

    public void increaseScore(Long playerId) {
        players.forEach(player -> {
            if(player.getId().equals(playerId)) {
                player.increaseMatchScore();
            }
        });
    }

    private Game getGamePrevious() {
        return games.get(games.size() - 2);
    }

    public Map.Entry<Long, Long> getPlayers(int first, int last) {
        return Map.entry(players.get(first).getId(), players.get(last).getId());
    }


    public Game getGamePlaying() {
        Game game = games.get(games.size() - 1);
        return game.isEnd() ? null : game;
    }

    public Game getLastGame() {
        if(games.isEmpty()) {
            return null;
        }
        return games.get(games.size() - 1);
    }

    public boolean checkPlayerExist(Long playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId) && !player.isLeaveRoom()) {
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
        GameRoomManager.getInstance().addPlayerToRoom(player.getId(), roomCode);

        if (isFull()) {
            setRoomStatusTypes(GameStatus.STARTED);
        }
    }

    public boolean checkFull() {
        return players.size() == MAX_PLAYER;
    }

    public void addSpectator(Player player) {
        spectators.add(player);
    }

    public boolean checkSpectatorExist(Long playerId) {
        for (Player player : spectators) {
            if (player.getId().equals(playerId) && !player.isLeaveRoom()) {
                return true;
            }
        }

        return false;
    }

    public void removePlayer(Long playerId) {
        players.forEach(player -> {
            if(player.getId().equals(playerId)) {
                player.setLeaveRoom(true);
            }
        } );

        GameRoomManager.getInstance().removePlayerFromRoom(playerId);

        if(players.isEmpty() && roomStatusTypes == GameStatus.STARTED) {
            setRoomStatusTypes(GameStatus.ENDED);
        }
    }

    public void removeSpectator(Long playerId) {
        spectators.removeIf(player -> player.getId().equals(playerId));
    }

    public void startGame() {
        Game game = getLastGame();
        game.startGame();
        game.setFirstAndSecondPlayerId(firstMove());
        game.setGameStatus(GameStatus.STARTED);
    }

    public void setEnd() {
        this.roomStatusTypes = GameStatus.ENDED;
    }

    public boolean isStarted() {
        return roomStatusTypes == GameStatus.STARTED;
    }

    public void setPlayerIsReady(boolean b) {
        players.forEach(player -> player.setReady(b));
    }

    public Player getPlayerById(Long playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }

        return null;
    }

    public boolean isAllPlayerIsReady() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }

        return true;
    }

    public void updateSeasonScore(PlayerSeason playerSeason){
        players.forEach(player -> {
            if(player.getId().equals(playerSeason.getPlayer().getId())) {
                player.setSeasonScore(playerSeason.getScore());
            }
        });
    }
}
