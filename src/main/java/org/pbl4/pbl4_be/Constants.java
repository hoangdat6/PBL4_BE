package org.pbl4.pbl4_be;

public class Constants {
    public static final Short MAX_PLAYER = 2;
    public static final Short MAX_ROOM = 10;
    public static final Short MAX_GAME = 10;

    public static final Short GAME_NOT_STARTED = 0;
    public static final Short GAME_STARTED = 1;
    public static final Short GAME_ENDED = 2;

    public static final String GAME_PROGRESS_TOPIC = "/topic/game-progress/";
    public static final String GAME_STATE_TOPIC = "/queue/game-state/";
    public static final String GAME_END_TOPIC = "/topic/end-game/";
    public static final String PLAY_AGAIN_TOPIC = "/topic/play-again/";
    public static final String GAME_START_TOPIC = "/queue/game-start/";
    public static final String SPECTATORS_TOPIC = "/queue/spectators/";

    // app
    public static final String SEND_MOVE = "/app/move/";
    public static final String SEND_PLAY_AGAIN = "/app/play-again/";
    public static final String SEND_WINNER = "/app/winner/";
}
