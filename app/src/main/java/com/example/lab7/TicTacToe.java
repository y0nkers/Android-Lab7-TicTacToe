package com.example.lab7;

public class TicTacToe {
    private boolean isGameActive = true;
    private int player = 1; // 1 = X, 2 = O
    private final int[][] field = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}; // 0 = null, 1 = X, 2 = O

    int[][] winPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    public boolean isActive() {
        return isGameActive;
    }

    public void changePlayer() {
        if (player == 1) player = 2;
        else player = 1;
    }

    public int getPlayer() {
        return player;
    }

    public void set(int i, int j) {
        field[i][j] = player;
    }

    public int get(int i, int j) {
        return field[i][j];
    }

    public int getStatus() {
        // Convert 2d array to 1d
        int[] array = new int[9];
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                array[i * 3 + j] = field[i][j];

        for (int i = 0; i < winPositions.length; ++i) {
            if (array[winPositions[i][0]] == array[winPositions[i][1]] && array[winPositions[i][1]] == array[winPositions[i][2]] && array[winPositions[i][0]] != 0) {
                isGameActive = false;
                return i + 1;
            }
        }

        for (int[] row : field)
            for (int cell : row)
                if (cell == 0) return -1; // Game not over

        return 0; // Draw
    }

    public void restart() {
        player = 1;
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                field[i][j] = 0;
        isGameActive = true;
    }
}
