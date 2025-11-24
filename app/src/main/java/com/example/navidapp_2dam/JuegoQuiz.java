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
import androidx.navigation.Navigation; // Importante para navegar

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

        // Panel Final (Oculto al principio)
        layoutFinal = view.findViewById(R.id.panelFinal);
        tvMensajeFinal = view.findViewById(R.id.tvMensajeFinal);
        tvSubtituloFinal = view.findViewById(R.id.tvSubtituloFinal);
        btnSalir = view.findViewById(R.id.btnSalir);

        // 3. Configurar juego
        cargarPreguntas();
        mostrarPregunta();

        // Listeners de las opciones
        btnOp1.setOnClickListener(v -> verificarRespuesta(0));
        btnOp2.setOnClickListener(v -> verificarRespuesta(1));
        btnOp3.setOnClickListener(v -> verificarRespuesta(2));

        // 4. BOTÓN FINAL: IR A LA PANTALLA DE FELICITACIÓN
        btnSalir.setOnClickListener(v -> {
            // Preparamos el nombre para que la pantalla final sepa de quién pedir el tiempo a la API
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);

            // NAVEGAMOS AL FINAL
            Navigation.findNavController(view).navigate(R.id.action_quiz_to_final, bundle);
        });
    }

    private void cargarPreguntas() {
        listaPreguntas = new ArrayList<>();
        // Pregunta 1
        listaPreguntas.add(new Pregunta(
                "¿Qué motor acabas de arreglar?",
                "Motor V8", "Motor ZIP", "Motor Diésel",
                1 // La correcta es la opción 1 (Motor ZIP)
        ));
        // Pregunta 2
        listaPreguntas.add(new Pregunta(
                "¿Qué buscamos con el escáner QR?",
                "Pistas escondidas", "Pokemons", "Wifi gratis",
                0
        ));
        // Pregunta 3
        listaPreguntas.add(new Pregunta(
                "¿Quién ayuda a Santa a hacer juguetes?",
                "Los Gnomos", "Los Elfos", "Los Minions",
                1
        ));
        // Pregunta 4
        listaPreguntas.add(new Pregunta(
                "¿Qué animal tira del trineo?",
                "Caballos", "Perros", "Renos",
                2
        ));
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

    private void verificarRespuesta(int indiceSeleccionado) {
        Pregunta p = listaPreguntas.get(indicePregunta);

        if (indiceSeleccionado == p.indiceCorrecto) {
            puntuacion++;
            Toast.makeText(getContext(), "¡Correcto! ✅", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Fallaste ❌", Toast.LENGTH_SHORT).show();
        }

        tvPuntos.setText("Puntos: " + puntuacion);
        indicePregunta++;
        // Pasamos a la siguiente pregunta
        mostrarPregunta();
    }

    private void terminarJuego() {
        // Ocultamos el juego y mostramos el panel final
        layoutFinal.setVisibility(View.VISIBLE);

        // Desactivamos los botones del fondo para que no se puedan pulsar
        btnOp1.setEnabled(false);
        btnOp2.setEnabled(false);
        btnOp3.setEnabled(false);

        tvMensajeFinal.setText("¡FIN, " + nombreJugador.toUpperCase() + "!");
        tvSubtituloFinal.setText("Has conseguido " + puntuacion + " de " + listaPreguntas.size() + " aciertos.");
    }

    // Clase interna para definir las preguntas
    private static class Pregunta {
        String texto;
        String[] opciones;
        int indiceCorrecto;

        public Pregunta(String t, String op1, String op2, String op3, int correcta) {
            this.texto = t;
            this.opciones = new String[]{op1, op2, op3};
            this.indiceCorrecto = correcta;
        }
    }
}