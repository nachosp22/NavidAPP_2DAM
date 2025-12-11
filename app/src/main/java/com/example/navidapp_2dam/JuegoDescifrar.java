package com.example.navidapp_2dam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class JuegoDescifrar extends Fragment {

    private String nombreJugador;
    private JuegoCifrado motorCifrado; // Instancia de tu clase lógica

    // Respuestas válidas a la pregunta del turrón
    private static final String RESPUESTA_1 = "ALMENDRAS";
    private static final String RESPUESTA_2 = "ALMENDRA";

    public JuegoDescifrar() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_juego_descifrar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar lógica
        motorCifrado = new JuegoCifrado();

        if (getArguments() != null) {
            nombreJugador = getArguments().getString("nombreJugador", "Aventurero");
        }

        // --- VINCULAR VISTAS ---
        // 1. Parte del Decodificador
        EditText etEntradaCifrada = view.findViewById(R.id.etEntradaCifrada);
        Button btnDecodificar = view.findViewById(R.id.btnDecodificar);
        TextView tvResultado = view.findViewById(R.id.tvResultadoDecodificado);

        // 2. Parte de la Respuesta
        EditText etRespuestaFinal = view.findViewById(R.id.etRespuestaFinal);
        Button btnEnviar = view.findViewById(R.id.btnEnviarRespuesta);

        // --- LÓGICA DEL DECODIFICADOR ---
        btnDecodificar.setOnClickListener(v -> {
            String textoCifrado = etEntradaCifrada.getText().toString().trim();
            if (!textoCifrado.isEmpty()) {
                // Usamos TU clase JuegoCifrado para traducir
                String textoPlano = motorCifrado.descifrarPalabra(textoCifrado);
                tvResultado.setText("Resultado: " + textoPlano);
            } else {
                Toast.makeText(getContext(), "Escribe algo para traducir", Toast.LENGTH_SHORT).show();
            }
        });

        // --- LÓGICA DE RESPUESTA A SANTA ---
        btnEnviar.setOnClickListener(v -> {
            String respuesta = etRespuestaFinal.getText().toString().trim().toUpperCase();

            // Validamos la respuesta
            if (respuesta.contains(RESPUESTA_1) || respuesta.contains(RESPUESTA_2)) {

                Toast.makeText(getContext(), "SANTA: ¡Ah, asi que esta echo de almendras almendras! ¡Qué rico! 🇪🇸", Toast.LENGTH_LONG).show();

                // Pasar al siguiente nivel (Sopa de Letras)
                Bundle bundle = new Bundle();
                bundle.putString("nombreJugador", nombreJugador);
                Navigation.findNavController(view).navigate(R.id.action_descifrar_to_dialogoHambre, bundle);

            } else {
                Toast.makeText(getContext(), "Santa te mira raro... Pista: Es un fruto seco.", Toast.LENGTH_SHORT).show();
                etRespuestaFinal.setText("");
            }
        });
    }
}