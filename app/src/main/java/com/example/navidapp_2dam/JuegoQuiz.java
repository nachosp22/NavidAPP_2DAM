package com.example.navidapp_2dam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

public class JuegoQuiz extends Fragment {

    // Componentes UI
    private TextView tvPregunta, tvPuntos, tvMensajeFinal, tvSubtituloFinal;
    private Button btnOp1, btnOp2, btnOp3, btnSalir;
    private LinearLayout layoutFinal;

    // Datos del juego
    private List<Pregunta> listaPreguntas;
    private int indicePregunta = 0;
    private int puntuacion = 0;
    private String nombreJugador;

    // Configuración de dificultad
    private static final int MINIMO_ACIERTOS = 3;

    public JuegoQuiz() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_juego_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Recuperar nombre
        nombreJugador = "Aventurero";
        if (getArguments() != null) {
            nombreJugador = getArguments().getString("nombreJugador", "Aventurero");
        }

        // 2. Vincular vistas
        tvPregunta = view.findViewById(R.id.tvPregunta);
        tvPuntos = view.findViewById(R.id.tvPuntuacion);
        btnOp1 = view.findViewById(R.id.btnOp1);
        btnOp2 = view.findViewById(R.id.btnOp2);
        btnOp3 = view.findViewById(R.id.btnOp3);

        layoutFinal = view.findViewById(R.id.panelFinal);
        tvMensajeFinal = view.findViewById(R.id.tvMensajeFinal);
        tvSubtituloFinal = view.findViewById(R.id.tvSubtituloFinal);
        btnSalir = view.findViewById(R.id.btnSalir);

        // 3. Iniciar juego
        cargarPreguntas();
        mostrarPregunta();

        // Listeners
        btnOp1.setOnClickListener(v -> verificarRespuesta(0));
        btnOp2.setOnClickListener(v -> verificarRespuesta(1));
        btnOp3.setOnClickListener(v -> verificarRespuesta(2));

        // NOTA: El listener de btnSalir se define en terminarJuego() según el resultado
    }

    private void cargarPreguntas() {
        listaPreguntas = new ArrayList<>();
        // TUS PREGUNTAS PERSONALIZADAS
        listaPreguntas.add(new Pregunta("¿De que esta hecho el turrón?", "Avellanas", "Almendras", "Langostinos", 1));
        listaPreguntas.add(new Pregunta("¿Cuales son los nombres de los Elfos de Santa?", "Misco y Tupu", "Javo y Java", "Epi y Blas", 0));
        listaPreguntas.add(new Pregunta("¿Que representa el logo de Java?", "Una serpiente", "Una taza de cafe", "Un minion", 1));
        listaPreguntas.add(new Pregunta("¿Cual de estos alimentos no forma parte del menu de Santa?", "Cordero", "Gambas", "Pavo", 0));
        listaPreguntas.add(new Pregunta("El Grinch es del mismo color que el logo de un Sistema Operativo ¿Cual?", "Android", "Linux", "Windows", 0));
    }

    private void mostrarPregunta() {
        if (indicePregunta < listaPreguntas.size()) {
            Pregunta p = listaPreguntas.get(indicePregunta);
            tvPregunta.setText(p.texto);
            btnOp1.setText(p.opciones[0]);
            btnOp2.setText(p.opciones[1]);
            btnOp3.setText(p.opciones[2]);
        } else {
            terminarJuego();
        }
    }

    private void verificarRespuesta(int index) {
        if (index == listaPreguntas.get(indicePregunta).indiceCorrecto) puntuacion++;
        tvPuntos.setText("Puntos: " + puntuacion);
        indicePregunta++;
        mostrarPregunta();
    }

    private void terminarJuego() {
        layoutFinal.setVisibility(View.VISIBLE);
        // Bloquear botones de fondo
        btnOp1.setEnabled(false); btnOp2.setEnabled(false); btnOp3.setEnabled(false);

        // LÓGICA DE APROBADO / SUSPENSO
        if (puntuacion >= MINIMO_ACIERTOS) {
            // -- GANADOR --
            tvMensajeFinal.setText("¡HAS GANADO!");
            tvSubtituloFinal.setText("¡Felicidades " + nombreJugador + "!\nHas recuperado los regalos (" + puntuacion + "/" + listaPreguntas.size() + ")");

            btnSalir.setText("TERMINAR AVENTURA 🎁");
            btnSalir.setBackgroundTintList(getContext().getColorStateList(android.R.color.holo_green_dark));

            // Listener para ir al final
            btnSalir.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("nombreJugador", nombreJugador);
                Navigation.findNavController(getView()).navigate(R.id.action_quiz_to_final, bundle);
            });

        } else {
            // -- PERDEDOR --
            tvMensajeFinal.setText("¡OH NO!");
            tvSubtituloFinal.setText("El Grinch se escapa...\nNecesitas al menos 3 aciertos.\nTienes: " + puntuacion);

            btnSalir.setText("🔄 REINTENTAR");
            btnSalir.setBackgroundTintList(getContext().getColorStateList(android.R.color.holo_red_dark));

            // Listener para reiniciar
            btnSalir.setOnClickListener(v -> {
                reiniciarNivel();
            });
        }
    }

    private void reiniciarNivel() {
        // Reseteamos el juego
        indicePregunta = 0;
        puntuacion = 0;
        tvPuntos.setText("Puntos: 0");

        // Ocultamos panel y reactivamos botones
        layoutFinal.setVisibility(View.GONE);
        btnOp1.setEnabled(true);
        btnOp2.setEnabled(true);
        btnOp3.setEnabled(true);

        // Empezamos de nuevo
        mostrarPregunta();
    }

    private static class Pregunta {
        String texto; String[] opciones; int indiceCorrecto;
        public Pregunta(String t, String o1, String o2, String o3, int c) {
            this.texto = t; this.opciones = new String[]{o1, o2, o3}; this.indiceCorrecto = c;
        }
    }
}