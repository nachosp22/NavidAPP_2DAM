package com.example.navidapp_2dam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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

        // --- 4. BOTÓN FINAL: CALCULAR TIEMPO Y NAVEGAR ---
        btnSalir.setOnClickListener(v -> {
            // Calculamos la diferencia de tiempo (Ahora - Inicio)
            long tiempoFin = System.currentTimeMillis();
            long tiempoJugado = (tiempoFin - Constantes.TIEMPO_INICIO) / 1000;

            // Si te saltaste el login, ponemos 0 para que no salga negativo
            if (Constantes.TIEMPO_INICIO == 0) tiempoJugado = 0;

            // Preparamos los datos para la pantalla final
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);
            bundle.putLong("tiempoFinal", tiempoJugado); // Enviamos el tiempo calculado

            // Navegamos
            Navigation.findNavController(view).navigate(R.id.action_quiz_to_final, bundle);
        });
    }

    private void cargarPreguntas() {
        listaPreguntas = new ArrayList<>();
        listaPreguntas.add(new Pregunta("¿Qué motor arreglaste?", "V8", "ZIP", "Diesel", 1));
        listaPreguntas.add(new Pregunta("¿Qué buscamos con QR?", "Pistas", "Pokemons", "Wifi", 0));
        listaPreguntas.add(new Pregunta("¿Ayudantes de Santa?", "Gnomos", "Elfos", "Minions", 1));
        listaPreguntas.add(new Pregunta("¿Animal del trineo?", "Caballo", "Perro", "Reno", 2));
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
        btnOp1.setEnabled(false); btnOp2.setEnabled(false); btnOp3.setEnabled(false);

        tvMensajeFinal.setText("¡FIN, " + nombreJugador + "!");
        tvSubtituloFinal.setText("Has conseguido " + puntuacion + " de " + listaPreguntas.size() + " aciertos.");
    }

    private static class Pregunta {
        String texto; String[] opciones; int indiceCorrecto;
        public Pregunta(String t, String o1, String o2, String o3, int c) {
            this.texto = t; this.opciones = new String[]{o1, o2, o3}; this.indiceCorrecto = c;
        }
    }
}