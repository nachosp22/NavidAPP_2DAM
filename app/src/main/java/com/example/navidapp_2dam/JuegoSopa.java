package com.example.navidapp_2dam;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
// import android.widget.Toast; // Ya no lo necesitamos
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Importante para navegar

// Importamos las clases de tu paquete 'sopaletras'
import com.example.navidapp_2dam.sopaletras.WordSearchLogic;
import com.example.navidapp_2dam.sopaletras.WordSearchView;

import java.util.Arrays;
import java.util.List;

public class JuegoSopa extends Fragment {

    private TextView txtCounter;
    private Button btnNextLevel;
    private List<String> words;
    private String nombreJugador;

    public JuegoSopa() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_juego_sopa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Recuperar nombre
        nombreJugador = "Aventurero";
        if (getArguments() != null) {
            nombreJugador = getArguments().getString("nombreJugador", "Aventurero");
        }

        // 2. Referencias UI
        txtCounter = view.findViewById(R.id.txt_counter);
        btnNextLevel = view.findViewById(R.id.btn_next_level);
        FrameLayout containerLayout = view.findViewById(R.id.game_container);

        // 3. Configuración del Juego (8x8)
        words = Arrays.asList("NAVIDAD", "RENO", "NIEVE", "ELFOS");
        WordSearchLogic logic = new WordSearchLogic(8, words);

        updateCounter(0);

        // 4. Crear la vista del juego y añadirla al FrameLayout
        WordSearchView gameView = new WordSearchView(requireContext(), logic);
        gameView.setOnGameEventListener(new WordSearchView.OnGameEventListener() {
            @Override
            public void onWordFound(int currentCount) {
                updateCounter(currentCount);
            }

            @Override
            public void onGameWon() {
                txtCounter.setText("¡BIEN TRABAJAO MOSTRI!");
                txtCounter.setTextColor(Color.RED);
                btnNextLevel.setVisibility(View.VISIBLE); // Mostramos botón
            }
        });

        containerLayout.addView(gameView);

        // 5. BOTÓN SIGUIENTE (Sin Toast, directo al Quiz)
        btnNextLevel.setOnClickListener(v -> {
            // Preparamos el nombre para el siguiente nivel
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);

            // Navegar directamente
            Navigation.findNavController(view).navigate(R.id.action_sopa_to_quiz, bundle);
        });
    }

    private void updateCounter(int current) {
        txtCounter.setText("Palabras encontradas: " + current + "/" + words.size());
    }
}