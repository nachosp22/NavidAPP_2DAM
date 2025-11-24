package com.example.navidapp_2dam.sopaletras;

import java.util.*;

public class WordSearchLogic {
    private char[][] board;
    private int size;
    private List<String> wordsToFind;
    private Set<String> foundWords;
    private List<FoundWordLine> foundLines;
    private Random random = new Random();

    private final int[][] directions = {
            {1, 0}, {0, 1}, {1, 1}, {1, -1}
    };

    public WordSearchLogic(int size, List<String> words) {
        this.size = size;
        this.wordsToFind = new ArrayList<>();
        for(String w : words) this.wordsToFind.add(w.toUpperCase());

        this.foundWords = new HashSet<>();
        this.foundLines = new ArrayList<>();
        this.board = new char[size][size];

        generateBoard();
    }

    private void generateBoard() {
        for (int i = 0; i < size; i++) Arrays.fill(board[i], ' ');
        for (String word : wordsToFind) placeWord(word);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == ' ') board[i][j] = (char) ('A' + random.nextInt(26));
            }
        }
    }

    private void placeWord(String word) {
        boolean placed = false;
        int attempts = 0;
        while (!placed && attempts < 100) {
            int dirIdx = random.nextInt(directions.length);
            int dx = directions[dirIdx][0];
            int dy = directions[dirIdx][1];
            int startRow = random.nextInt(size);
            int startCol = random.nextInt(size);

            if (canPlace(word, startRow, startCol, dx, dy)) {
                for (int i = 0; i < word.length(); i++) {
                    board[startRow + i * dy][startCol + i * dx] = word.charAt(i);
                }
                placed = true;
            }
            attempts++;
        }
    }

    private boolean canPlace(String word, int row, int col, int dx, int dy) {
        int endRow = row + (word.length() - 1) * dy;
        int endCol = col + (word.length() - 1) * dx;
        if (endRow < 0 || endRow >= size || endCol < 0 || endCol >= size) return false;

        for (int i = 0; i < word.length(); i++) {
            char currentRowChar = board[row + i * dy][col + i * dx];
            if (currentRowChar != ' ' && currentRowChar != word.charAt(i)) return false;
        }
        return true;
    }

    public boolean checkSelection(int r1, int c1, int r2, int c2) {
        int dr = Integer.compare(r2, r1);
        int dc = Integer.compare(c2, c1);
        if (dr == 0 && dc == 0) return false;

        StringBuilder sb = new StringBuilder();
        int len = Math.max(Math.abs(r2 - r1), Math.abs(c2 - c1)) + 1;
        int currR = r1, currC = c1;

        for (int i = 0; i < len; i++) {
            sb.append(board[currR][currC]);
            currR += dr;
            currC += dc;
        }

        String selected = sb.toString();
        String reversed = sb.reverse().toString();
        selected = sb.reverse().toString();

        if (wordsToFind.contains(selected) && !foundWords.contains(selected)) {
            foundWords.add(selected);
            foundLines.add(new FoundWordLine(r1, c1, r2, c2));
            return true;
        } else if (wordsToFind.contains(reversed) && !foundWords.contains(reversed)) {
            foundWords.add(reversed);
            foundLines.add(new FoundWordLine(r1, c1, r2, c2));
            return true;
        }
        return false;
    }

    public char getCell(int row, int col) { return board[row][col]; }
    public int getSize() { return size; }
    public boolean isGameFinished() { return foundWords.size() == wordsToFind.size(); }
    public List<FoundWordLine> getFoundLines() { return foundLines; }
    public Set<String> getFoundWords() { return foundWords; }
}