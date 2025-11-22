package com.example.navidapp_2dam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableroView extends View {

    // --- 1. INTERFAZ PARA AVISAR SI GANAMOS ---
    // Esto es como una alarma que salta cuando se completa el nivel
    public interface OnJuegoGanadoListener {
        void onGanado();
    }
    private OnJuegoGanadoListener listener;

    // Método para que el Fragmento se suscriba a la alarma
    public void setOnJuegoGanadoListener(OnJuegoGanadoListener l) {
        this.listener = l;
    }

    // --- CONFIGURACIÓN DEL NIVEL ---
    private int[][] nivel = {
            {1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 7, 0},
            {0, 4, 0, 8, 0, 0},
            {0, 0, 6, 0, 5, 0},
            {0, 3, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 2}
    };

    private int numColumnas = 6;
    private int numFilas = 6;
    private float anchoCelda, altoCelda;
    private int maxNumeroNivel = 0;

    // Pinceles para dibujar
    private Paint paintGrid, paintBorde, paintTexto, paintBastonBase, paintBastonRayas;

    // Lógica del camino
    private List<Point> camino = new ArrayList<>();
    private int siguienteNumeroEsperado = 2;

    // Clase auxiliar para guardar coordenadas (fila, columna)
    private static class Point {
        int fila, col;
        Point(int r, int c) { fila = r; col = c; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return fila == point.fila && col == point.col;
        }
        @Override
        public int hashCode() { return Objects.hash(fila, col); }
    }

    public TableroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Calcular cuál es el número más alto del nivel
        for (int[] fila : nivel) {
            for (int celda : fila) {
                if (celda > maxNumeroNivel) maxNumeroNivel = celda;
            }
        }

        // Configurar los pinceles (colores, grosores...)
        paintGrid = new Paint();
        paintGrid.setColor(Color.parseColor("#D4AF37")); // Dorado
        paintGrid.setStrokeWidth(2);
        paintGrid.setStyle(Paint.Style.STROKE);

        paintBorde = new Paint();
        paintBorde.setColor(Color.parseColor("#1B5E20")); // Verde Navidad
        paintBorde.setStrokeWidth(16); // Grosor del marco
        paintBorde.setStyle(Paint.Style.STROKE);

        float grosorBaston = 50f;
        paintBastonBase = new Paint();
        paintBastonBase.setColor(Color.WHITE);
        paintBastonBase.setStrokeWidth(grosorBaston);
        paintBastonBase.setStyle(Paint.Style.STROKE);
        paintBastonBase.setStrokeCap(Paint.Cap.ROUND);
        paintBastonBase.setStrokeJoin(Paint.Join.ROUND);
        paintBastonBase.setAntiAlias(true);

        paintBastonRayas = new Paint();
        paintBastonRayas.setColor(Color.parseColor("#D32F2F")); // Rojo Navidad
        paintBastonRayas.setStrokeWidth(grosorBaston - 6);
        paintBastonRayas.setStyle(Paint.Style.STROKE);
        paintBastonRayas.setStrokeCap(Paint.Cap.BUTT);
        paintBastonRayas.setStrokeJoin(Paint.Join.ROUND);
        // Efecto de rayas discontinuas
        paintBastonRayas.setPathEffect(new DashPathEffect(new float[]{35f, 35f}, 0));
        paintBastonRayas.setAntiAlias(true);

        paintTexto = new Paint();
        paintTexto.setColor(Color.parseColor("#1B5E20"));
        paintTexto.setTextSize(65);
        paintTexto.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        paintTexto.setTextAlign(Paint.Align.CENTER);
        paintTexto.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Calcular tamaño de las celdas según el tamaño de la pantalla
        anchoCelda = w / (float) numColumnas;
        altoCelda = h / (float) numFilas;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. Dibujar Rejilla
        for (int i = 1; i < numColumnas; i++) canvas.drawLine(i * anchoCelda, 0, i * anchoCelda, getHeight(), paintGrid);
        for (int i = 1; i < numFilas; i++) canvas.drawLine(0, i * altoCelda, getWidth(), i * altoCelda, paintGrid);

        // 2. Dibujar Marco Verde
        float margen = paintBorde.getStrokeWidth() / 2;
        canvas.drawRect(margen, margen, getWidth() - margen, getHeight() - margen, paintBorde);

        // 3. Dibujar Números
        float ajusteY = ((paintTexto.descent() + paintTexto.ascent()) / 2);
        for (int fila = 0; fila < numFilas; fila++) {
            for (int col = 0; col < numColumnas; col++) {
                int valor = nivel[fila][col];
                if (valor > 0) {
                    float x = (col * anchoCelda) + (anchoCelda / 2);
                    float y = (fila * altoCelda) + (altoCelda / 2) - ajusteY;
                    canvas.drawText(String.valueOf(valor), x, y, paintTexto);
                }
            }
        }

        // 4. Dibujar Camino (Bastón)
        if (camino.size() > 0) {
            Path path = new Path();
            float startX = (camino.get(0).col * anchoCelda) + (anchoCelda / 2);
            float startY = (camino.get(0).fila * altoCelda) + (altoCelda / 2);
            path.moveTo(startX, startY);
            for (int i = 1; i < camino.size(); i++) {
                float x = (camino.get(i).col * anchoCelda) + (anchoCelda / 2);
                float y = (camino.get(i).fila * altoCelda) + (altoCelda / 2);
                path.lineTo(x, y);
            }
            // Dibujamos dos veces para el efecto de bastón de caramelo
            canvas.drawPath(path, paintBastonBase);
            canvas.drawPath(path, paintBastonRayas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        // Convertir coordenadas de pantalla a fila/columna
        int col = (int) (x / anchoCelda);
        int fila = (int) (y / altoCelda);

        // Si estamos fuera del tablero, ignorar
        if (col < 0 || col >= numColumnas || fila < 0 || fila >= numFilas) return true;
        Point puntoActual = new Point(fila, col);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Solo empezamos si tocamos el número 1
                if (nivel[fila][col] == 1) {
                    camino.clear();
                    camino.add(puntoActual);
                    siguienteNumeroEsperado = 2;
                    invalidate(); // Redibujar
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (camino.isEmpty()) break;
                Point ultimo = camino.get(camino.size() - 1);
                // Si nos hemos movido a una celda nueva
                if (!ultimo.equals(puntoActual)) {
                    // Verificar que es vecina (arriba, abajo, izq, der)
                    boolean esVecino = (Math.abs(ultimo.fila - fila) + Math.abs(ultimo.col - col)) == 1;
                    // Verificar que no hemos pasado ya por ahí
                    boolean noVisitado = !camino.contains(puntoActual);

                    if (esVecino && noVisitado) {
                        int valorCasilla = nivel[fila][col];
                        if (valorCasilla == 0) {
                            // Es una casilla vacía, avanzamos
                            camino.add(puntoActual);
                            invalidate();
                        } else if (valorCasilla == siguienteNumeroEsperado) {
                            // Es el número correcto, avanzamos
                            camino.add(puntoActual);
                            siguienteNumeroEsperado++;
                            invalidate();
                        }
                        // Si es un número incorrecto, no hacemos nada (bloqueado)
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // Al levantar el dedo, comprobamos si ha ganado
                checkVictoria();
                break;
        }
        return true;
    }

    private void checkVictoria() {
        if (camino.isEmpty()) return;

        int totalCeldas = numFilas * numColumnas;
        // Condición 1: Haber llenado todas las celdas
        boolean todasCeldasUsadas = (camino.size() == totalCeldas);

        Point ultimoPunto = camino.get(camino.size() - 1);
        // Condición 2: Haber terminado en el número más alto
        int valorUltimaCasilla = nivel[ultimoPunto.fila][ultimoPunto.col];

        if (todasCeldasUsadas && valorUltimaCasilla == maxNumeroNivel) {
            // ¡VICTORIA! Avisamos al listener si alguien está escuchando
            if (listener != null) {
                listener.onGanado();
            }
        }
    }

    public void reiniciar() {
        camino.clear();
        siguienteNumeroEsperado = 2;
        invalidate();
    }
}