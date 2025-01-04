package org.pbl4.pbl4_be.services;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.controllers.dto.BoardDTO;
import org.pbl4.pbl4_be.models.Board;
import org.pbl4.pbl4_be.models.GameMove;

import java.util.*;

public class AIGameService {
    private final long[] points = {100, 10000, 1000000, 100000000};
    private final int n = 16;
    private short count;
    private final byte[][] board;
    private final boolean playerFirst;
    List<GameMove> moves;
    @Getter
    @Setter
    private boolean isPlayerTurn = true;
    private static final int[][] DIRECTIONS = {
            {1, 0}, {0, 1}, {1, 1}, {1, -1} // Dọc, Ngang, Chéo chính, Chéo phụ
    };
    @Getter
    @Setter
    private Long playerId;

    public AIGameService(boolean playerFirst) {
        board = new byte[n][n];
        moves = new ArrayList<>();
        count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = -1;
            }
        }
        this.playerFirst = playerFirst;
        if(!playerFirst) {
            board[n / 2][n / 2] = 0;
            moves.add(new GameMove(n / 2, n / 2, count));
            count++;
        }

    }

    boolean checkOver(int x, int y, byte[][] board, int dx, int dy) {
        byte length = 1, symbol = board[x][y];
        int n = board.length;
        for(int i = 1; i <= 4; i++){
            int nx = x - i * dx, ny = y - i * dy;
            if (nx < 0 || ny < 0 || nx >= n || ny >= n) break; // Ra khỏi bàn cờ
            if (board[nx][ny] == symbol) length++;
            else break;
        }

        for(int i = 1; i <= 4; i++){
            int nx = x + i * dx, ny = y + i * dy;
            if (nx < 0 || ny < 0 || nx >= n || ny >= n) break; // Ra khỏi bàn cờ
            if (board[nx][ny] == symbol) length++;
            else break;
        }
        return length >= 5;
    }

    boolean isGameOver(int x, int y, byte[][] board) {
        for (int[] dir : DIRECTIONS) {
            if (checkOver(x, y, board, dir[0], dir[1])) return true;
        }
        return false;
    }

    public long evaluate(int i, int j, byte r, byte[][] board, int stepX, int stepY) {
        long res = 1, check = 1, block = 0, S = 0;
        int n = board.length;

        // Kiểm tra trước điểm bắt đầu
        int prevX = i - stepX;
        int prevY = j - stepY;
        if (prevX >= 0 && prevX < n && prevY >= 0 && prevY < n && board[prevX][prevY] == r) return 0;
        if (prevX < 0 || prevX >= n || prevY < 0 || prevY >= n || board[prevX][prevY] == (r + 1) % 2) block++;

        // Duyệt theo hướng được chỉ định
        for (int k = 1; k < 5; k++) {
            int newX = i + k * stepX;
            int newY = j + k * stepY;
            if (newX >= 0 && newX < n && newY >= 0 && newY < n) {
                if (board[newX][newY] == r) {
                    if (check >= 0) res++;
                    else break;
                } else if (board[newX][newY] == -1) {
                    check--;
                } else {
                    block++;
                    break;
                }
            } else {
                block++;
                break;
            }
        }

        // Xử lý kết quả
        if (res >= 5) return Long.MAX_VALUE / 10;
        long div = (block == 0 ? 1 : (block == 1 ? 100 : 1000000000));
        S += res/2 * points[(int)(res - 1)] / div;
        return S;
    }


    public long centralPositionHeuristic(int i, int j, int n) {
        int center = n / 2;
        return -(Math.abs(i - center) + Math.abs(j - center)); // Càng gần trung tâm càng tốt
    }
    public long evaluatePosition(int i, int j, byte player, byte[][] board) {
        long S = 0;
        for (int[] dir : DIRECTIONS) {
            S += evaluate(i, j, player, board, dir[0], dir[1]);
        }
        S += centralPositionHeuristic(i, j, n);
        return S;
    }


    public long heuristicEvaluate(byte player, byte bot, byte[][] board) {
        long A = 0, B = 0;

        for(GameMove move : moves) {
            int i = move.getRow(), j = move.getCol();
            if (board[i][j] == player) {
                A += evaluatePosition(i, j, player, board);
            } else {
                B += evaluatePosition(i, j, bot, board);
            }
        }

        return B - A;
    }

    public List<int[]> generateMoves(byte[][] board) {
        Set<int[]> candidateMoves = new HashSet<>();
        for (GameMove move : moves) {
            int row = move.getRow(), col = move.getCol();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int x = row + dx, y = col + dy;
                    if (x >= 0 && y >= 0 && x < board.length && y < board.length && board[x][y] == -1) {
                        candidateMoves.add(new int[]{x, y});
                    }
                }
            }
        }
        return new ArrayList<>(candidateMoves);
    }
    public long alphaBeta(int i, int j, int depth, long alpha, long beta, boolean maximizingPlayer, byte player, byte bot, byte[][] board) {
        if (depth == 0) {
            return heuristicEvaluate(player, bot, board);
        }

        if(isGameOver(i, j, board)) {
            return board[i][j] == player ? Long.MIN_VALUE : Long.MAX_VALUE;
        }

        if (maximizingPlayer) {
            long maxEval = Long.MIN_VALUE;
            for (int[] move : generateMoves(board)) { // Lấy các nước đi có thể
                board[move[0]][move[1]] = bot;
                moves.add(new GameMove(move[0], move[1], count));
                long eval = alphaBeta(move[0], move[1], depth - 1, alpha, beta, false, player, bot, board);
                moves.remove(moves.size() - 1);
                board[move[0]][move[1]] = -1;
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Cắt tỉa
            }
            return maxEval;

        } else {
            long minEval = Long.MAX_VALUE;
            for (int[] move : generateMoves(board)) {
                board[move[0]][move[1]] = player;
                moves.add(new GameMove(move[0], move[1], count));
                long eval = alphaBeta(move[0], move[1], depth - 1, alpha, beta, true, player, bot, board);
                moves.remove(moves.size() - 1);
                board[move[0]][move[1]] = -1;
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Cắt tỉa
            }
            return minEval;
        }
    }
    public GameMove playGame(int playerRow, int playerCol) {
        byte playerSymbol = playerFirst ? (byte) 0 : (byte) 1;
        byte botSymbol = playerFirst ? (byte) 1 : (byte) 0;
        byte winner = -1;
        int depth = 2;
        GameMove gameMove = new GameMove();
        if (board[playerRow][playerCol] == -1) {
            board[playerRow][playerCol] = playerSymbol;
            moves.add(new GameMove(playerRow, playerCol, count));
            count++;
            if (isGameOver(playerRow, playerCol, board)) {
                System.out.println("End game");
                winner = playerSymbol;
                gameMove = new GameMove(-1, -1, count);
            } else {
                long bestScore = Long.MIN_VALUE;
                int bestRow = -1, bestCol = -1;

                for (int[] move : generateMoves(board)) {
                    board[move[0]][move[1]] = botSymbol;
                    moves.add(new GameMove(move[0], move[1], count));
                    long moveScore = alphaBeta(move[0], move[1], depth, Long.MIN_VALUE, Long.MAX_VALUE, false, playerSymbol, botSymbol, board);
                    moves.remove(moves.size() - 1);
                    board[move[0]][move[1]] = -1;

                    if (moveScore > bestScore) {
                        bestScore = moveScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }

                board[bestRow][bestCol] = botSymbol;
                gameMove.setRow(bestRow);
                gameMove.setCol(bestCol);
                gameMove.setNthMove(count);
                moves.add(gameMove);
                count++;
                if (isGameOver(bestRow, bestCol, board)) {
                    System.out.println("End game");
                    winner = botSymbol;
                }
            }
        } else {
            System.out.println("Invalid player move");
        }
        System.out.println(gameMove.getRow() + " " + gameMove.getCol());
        gameMove.setWin(winner != -1);
        return gameMove;
    }

    public void addMove(int row, int col) {
        GameMove gameMove = new GameMove(row, col, count);
        moves.add(gameMove);
    }

    private String randomRoomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public GameMove getLastMove() {
        return moves.isEmpty() ? null : moves.get(moves.size() - 1);
    }

    public String getRoomCode() {
        return randomRoomCode();
    }

    public BoardDTO getBoard() {
        return new BoardDTO(new Board(board, n, 5));
    }

    public short getNthMove() {
        return count;
    }

}
