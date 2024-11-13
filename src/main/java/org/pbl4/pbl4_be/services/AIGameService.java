package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.models.GameMove;

public class AIGameService {
    private final long[] points = {100, 10000, 1000000, 100000000};
    private final int n = 16;
    private final long inf = (long)1e18;
    private short count;
    private byte[][] board;
    private boolean playerFirst;

    public AIGameService(boolean playerFirst) {
        board = new byte[n][n];
        count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = -1;
            }
        }
        this.playerFirst = playerFirst;
        if(!playerFirst) {
            board[n / 2][n / 2] = 'X';
            count++;
        }

    }

    public boolean checkEnd(int i, int j, byte symbol, byte[][] board) {
        int row = 1, col = 1, diag1 = 1, diag2 = 1;

        for (int k = 1; k < 5; k++) {
            if (j + k < n && board[i][j + k] == symbol) row++;
            else break;
        }
        for (int k = 1; k < 5; k++) {
            if (j - k >= 0 && board[i][j - k] == symbol) row++;
            else break;
        }

        for (int k = 1; k < 5; k++) {
            if (i + k < n && board[i + k][j] == symbol) col++;
            else break;
        }
        for (int k = 1; k < 5; k++) {
            if (i - k >= 0 && board[i - k][j] == symbol) col++;
            else break;
        }

        for (int k = 1; k < 5; k++) {
            if (i + k < n && j + k < n && board[i + k][j + k] == symbol) diag1++;
            else break;
        }
        for (int k = 1; k < 5; k++) {
            if (i - k >= 0 && j - k >= 0 && board[i - k][j - k] == symbol) diag1++;
            else break;
        }

        for (int k = 1; k < 5; k++) {
            if (i + k < n && j - k >= 0 && board[i + k][j - k] == symbol) diag2++;
            else break;
        }
        for (int k = 1; k < 5; k++) {
            if (i - k >= 0 && j + k < n && board[i - k][j + k] == symbol) diag2++;
            else break;
        }

        return row >= 5 || col >= 5 || diag1 >= 5 || diag2 >= 5;
    }

    public long evaluateRow(int i, int j, byte r, byte[][] board) {
        long res = 1, check = 1, block = 0, S = 0;
        if (j - 1 >= 0 && board[i][j - 1] == r) return 0;
        if (j - 1 < 0 || board[i][j - 1] == (r + 1)%2) block++;
        for (int k = 1; k < 5; k++) {
            if (j + k < n) {
                if (board[i][j + k] == r) {
                    if (check >= 0) res++;
                    else break;
                } else if (board[i][j + k] == -1) {
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

        if (res == 5) return inf;
        long div = (block == 0 ? 1 : (block == 1 ? 100 : 1000000000));
        S += points[(int)(res - 1)] / div;
        return S;
    }

    public long evaluateCol(int i, int j, byte r, byte[][] board) {
        long res = 1, check = 1, block = 0, S = 0;
        if (i - 1 >= 0 && board[i - 1][j] == r) return 0;
        if (i - 1 < 0 || board[i - 1][j] == (r + 1)%2) block++;

        for (int k = 1; k < 5; k++) {
            if (i + k < n) {
                if (board[i + k][j] == r) {
                    if (check >= 0) res++;
                    else break;
                } else if (board[i + k][j] == -1) {
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

        if (res == 5) return inf;
        long div = (block == 0 ? 1 : (block == 1 ? 100 : 1000000000));
        S += points[(int)(res - 1)] / div;
        return S;
    }

    public long evaluateDiag1(int i, int j, byte r, byte[][] board) {
        long res = 1, check = 1, block = 0, S = 0;
        if (i - 1 >= 0 && j - 1 >= 0 && board[i - 1][j - 1] == r) return 0;
        if (i - 1 < 0 || j - 1 < 0 || board[i - 1][j - 1] == (r + 1)%2) block++;

        for (int k = 1; k < 5; k++) {
            if (i + k < n && j + k < n) {
                if (board[i + k][j + k] == r) {
                    if (check >= 0) res++;
                    else break;
                } else if (board[i + k][j + k] == -1) {
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

        if (res == 5) return inf;
        long div = (block == 0 ? 1 : (block == 1 ? 100 : 1000000000));
        S += points[(int)(res - 1)] / div;
        return S;
    }

    public long evaluateDiag2(int i, int j, byte r, byte[][] board) {
        long res = 1, check = 1, block = 0, S = 0;
        if (i - 1 >= 0 && j + 1 < n && board[i - 1][j + 1] == r) return 0;
        if (i - 1 < 0 || j + 1 >= n || board[i - 1][j + 1] == (r + 1)%2) block++;

        for (int k = 1; k < 5; k++) {
            if (i + k < n && j - k >= 0) {
                if (board[i + k][j - k] == r) {
                    if (check >= 0) res++;
                    else break;
                } else if (board[i + k][j - k] == -1) {
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

        if (res == 5) return inf;
        long div = (block == 0 ? 1 : (block == 1 ? 100 : 1000000000));
        S += points[(int)(res - 1)] / div;
        return S;
    }

    public boolean operations(int i, int j) {
        if(board[i][j] != -1) {
            return false;
        }
        board[i][j] = (byte)((count % 2 == 0) ? 1 : 0);
        count++;
        return true;
    }

    public long heuristicEvaluate(byte player, byte bot, int depth, byte[][] board) {
        long A = 0, B = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == -1) continue;

                if (board[i][j] == player) {
                    B += evaluateRow(i, j, player, board);
                    B += evaluateCol(i, j, player, board);
                    B += evaluateDiag1(i, j, player, board);
                    B += evaluateDiag2(i, j, player, board);
                } else {
                    A += evaluateRow(i, j, bot, board);
                    A += evaluateCol(i, j, bot, board);
                    A += evaluateDiag1(i, j, bot, board);
                    A += evaluateDiag2(i, j, bot, board);
                }
            }
        }

        return (depth == 0) ? A - 2 * B : A - B;
    }

    public long alphaBeta(int depth, long alpha, long beta, boolean maximizingPlayer, byte player, byte bot, int ii, int jj, byte[][] board) {
        if (depth == 0 || checkEnd(ii, jj, player, board)) {
            return heuristicEvaluate(player, bot, depth, board);
        }

        if (maximizingPlayer) {
            long maxEval = Long.MIN_VALUE;
            int l = (n - 1) / 2, t = (n - 1) / 2;
            int r = (n + 1) / 2, b = (n + 1) / 2;

            while (beta > alpha) {
                for (int i = l; i <= Math.min(r, n - 1) && t >= 0; i++) {
                    if (board[t][i] == -1) {
                        board[t][i] = bot;
                        long eval = alphaBeta(depth - 1, alpha, beta, false, player, bot, t, i, board);
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        board[t][i] = -1;
                        if (beta <= alpha) break;
                    }
                }
                if (beta <= alpha) break;
                l--;

                for (int i = t + 1; i <= Math.min(b, n - 1) && r < n; i++) {
                    if (board[i][r] == -1) {
                        board[i][r] = bot;
                        long eval = alphaBeta(depth - 1, alpha, beta, false, player, bot, i, r, board);
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        board[i][r] = -1;
                        if (beta <= alpha) break;
                    }
                }
                if (beta <= alpha) break;

                for (int i = r - 1; i >= Math.max(l, 0) && b < n; i--) {
                    if (board[b][i] == -1) {
                        board[b][i] = bot;
                        long eval = alphaBeta(depth - 1, alpha, beta, false, player, bot, b, i, board);
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        board[b][i] = -1;
                        if (beta <= alpha) break;
                    }
                }
                if (beta <= alpha) break;

                for (int i = b - 1; i >= Math.max(t, 0) && l >= 0; i--) {
                    if (board[i][l] == -1) {
                        board[i][l] = bot;
                        long eval = alphaBeta(depth - 1, alpha, beta, false, player, bot, i, l, board);
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        board[i][l] = -1;
                        if (beta <= alpha) break;
                    }
                }
                r++;
                t--;
                b++;
                if (l < 0 && r >= n && t < 0 && b >= n) break;
            }
            return maxEval;

        } else {
            long minEval = Long.MAX_VALUE;
            int l = (n - 1) / 2, t = (n - 1) / 2;
            int r = (n + 1) / 2, b = (n + 1) / 2;

            while (beta > alpha) {
                for (int i = l; i <= Math.min(r, n - 1) && t >= 0; i++) {
                    if (board[t][i] == -1) {
                        board[t][i] = player;
                        long eval = alphaBeta(depth - 1, alpha, beta, true, player, bot, t, i, board);
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        board[t][i] = -1;
                        if (beta <= alpha) break;
                    }
                }
                if (beta <= alpha) break;
                l--;

                for (int i = t + 1; i <= Math.min(b, n - 1) && r < n; i++) {
                    if (board[i][r] == -1) {
                        board[i][r] = player;
                        long eval = alphaBeta(depth - 1, alpha, beta, true, player, bot, i, r, board);
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        board[i][r] = -1;
                        if (beta <= alpha) break;
                    }
                }
                if (beta <= alpha) break;

                for (int i = r - 1; i >= Math.max(l, 0) && b < n; i--) {
                    if (board[b][i] == -1) {
                        board[b][i] = player;
                        long eval = alphaBeta(depth - 1, alpha, beta, true, player, bot, b, i, board);
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        board[b][i] = -1;
                        if (beta <= alpha) break;
                    }
                }
                if (beta <= alpha) break;

                for (int i = b - 1; i >= Math.max(t, 0) && l >= 0; i--) {
                    if (board[i][l] == -1) {
                        board[i][l] = player;
                        long eval = alphaBeta(depth - 1, alpha, beta, true, player, bot, i, l, board);
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        board[i][l] = -1;
                        if (beta <= alpha) break;
                    }
                }
                r++;
                t--;
                b++;
                if (l < 0 && r >= n && t < 0 && b >= n) break;
            }
            return minEval;
        }
    }
    public GameMove playGame(int playerRow, int playerCol) {
        byte playerSymbol = playerFirst ? (byte) 1 : (byte) 0;
        byte botSymbol = playerFirst ? (byte) 0 : (byte) 1;
        byte winner = -1;
        int depth = 2;
        GameMove gameMove = new GameMove();
        if (operations(playerRow, playerCol)) {
            if (checkEnd(playerRow, playerCol, playerSymbol, board)) {
                winner = playerSymbol;
            } else {
                // Khởi tạo các biến để lưu nước đi tốt nhất của bot
                long bestScore = Long.MIN_VALUE;
                int bestRow = -1, bestCol = -1;

                // Tìm kiếm nước đi tốt nhất của bot
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (board[i][j] == -1) {
                            board[i][j] = botSymbol;
                            long moveScore = alphaBeta(depth, Long.MIN_VALUE, Long.MAX_VALUE, false, playerSymbol, botSymbol, i, j, board);
                            board[i][j] = -1;

                            if (moveScore > bestScore) {
                                bestScore = moveScore;
                                bestRow = i;
                                bestCol = j;
                            }
                        }
                    }
                }

                // Đặt nước đi tốt nhất của bot lên bàn cờ
                operations(bestRow, bestCol);
                gameMove.setRow(bestRow);
                gameMove.setCol(bestCol);
                gameMove.setNthMove(count);
                if (checkEnd(bestRow, bestCol, botSymbol, board)) {
                    winner = botSymbol;
                }
            }
        } else {
            System.out.println("Invalid player move");
        }

        gameMove.setWin(winner != -1);
        return gameMove;
    }






}

