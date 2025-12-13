package com.example.navidapp_2dam.sopaletras;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class WordSearchView extends View {

    public interface OnGameEventListener {
        void onWordFound(int currentCount);
        void onGameWon();
    }

    private WordSearchLogic logic;
    private OnGameEventListener listener;

    private float cellSize;
    private Point startDrag, endDrag;
    private float boardWidth, boardHeight;
    private float offsetX, offsetY;

    // --- COLORES ACTUALIZADOS PARA EL TEMA ---
    // Fondo blanco casi opaco (240/255) para leer bien
    private final int COLOR_BG = Color.argb(240, 255, 255, 255);
    // Texto verde navidad oscuro
    private final int COLOR_TEXT = Color.parseColor("#1B5E20");
    // Selección roja semitransparente (Estilo Navidad)
    private final int COLOR_SELECTION = Color.argb(120, 211, 47, 47);
    // Palabra encontrada en Oro/Amarillo oscuro
    private final int COLOR_FOUND = Color.argb(180, 255, 193, 7);

    private Paint textPaint, selectionPaint, foundPaint;

    public WordSearchView(Context context, WordSearchLogic logic) {
        super(context);
        this.logic = logic;
        initPaints();
    }

    public void setOnGameEventListener(OnGameEventListener listener) {
        this.listener = listener;
    }

    private void initPaints() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(COLOR_TEXT);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        selectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectionPaint.setColor(COLOR_SELECTION);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeCap(Paint.Cap.ROUND);

        foundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foundPaint.setColor(COLOR_FOUND);
        foundPaint.setStyle(Paint.Style.STROKE);
        foundPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = logic.getSize();
        cellSize = (float) (w - 20) / size; // Margen pequeño interno
        boardWidth = cellSize * size;
        boardHeight = cellSize * size;
        offsetX = (w - boardWidth) / 2;
        offsetY = (h - boardHeight) / 2;

        textPaint.setTextSize(cellSize * 0.6f);
        selectionPaint.setStrokeWidth(cellSize * 0.6f); // Selección más ancha
        foundPaint.setStrokeWidth(cellSize * 0.6f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(COLOR_BG); // Dibuja el fondo del tablero

        // 1. Dibujar Letras
        for (int i = 0; i < logic.getSize(); i++) {
            for (int j = 0; j < logic.getSize(); j++) {
                float x = offsetX + j * cellSize;
                float y = offsetY + i * cellSize;
                float textX = x + cellSize / 2;
                float textY = y + cellSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2;
                canvas.drawText(String.valueOf(logic.getCell(i, j)), textX, textY, textPaint);
            }
        }

        // 2. Dibujar Encontradas
        for (FoundWordLine line : logic.getFoundLines()) {
            drawLine(canvas, line.c1, line.r1, line.c2, line.r2, foundPaint);
        }

        // 3. Dibujar Selección Actual
        if (startDrag != null && endDrag != null) {
            drawLine(canvas, startDrag.x, startDrag.y, endDrag.x, endDrag.y, selectionPaint);
        }
    }

    private void drawLine(Canvas canvas, int c1, int r1, int c2, int r2, Paint paint) {
        float x1 = offsetX + c1 * cellSize + cellSize / 2;
        float y1 = offsetY + r1 * cellSize + cellSize / 2;
        float x2 = offsetX + c2 * cellSize + cellSize / 2;
        float y2 = offsetY + r2 * cellSize + cellSize / 2;
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - offsetX;
        float y = event.getY() - offsetY;

        if (x < 0 || x > boardWidth || y < 0 || y > boardHeight) return true;

        int c = (int) (x / cellSize);
        int r = (int) (y / cellSize);

        c = Math.max(0, Math.min(logic.getSize() - 1, c));
        r = Math.max(0, Math.min(logic.getSize() - 1, r));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startDrag = new Point(c, r);
                endDrag = new Point(c, r);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (startDrag != null) {
                    endDrag = new Point(c, r);
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (startDrag != null) {
                    endDrag = new Point(c, r);
                    boolean found = logic.checkSelection(startDrag.y, startDrag.x, endDrag.y, endDrag.x);
                    if (found && listener != null) {
                        listener.onWordFound(logic.getFoundWords().size());
                        if (logic.isGameFinished()) {
                            listener.onGameWon();
                        }
                    }
                }
                startDrag = null;
                endDrag = null;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}