package com.example.navidapp_2dam;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JuegoQR extends Fragment {

    private TextView tvPista1, tvPista2, tvPista3, tvPista4, tvPista5;
    private Button btnSiguiente;

    // Lógica del juego
    private Set<String> codigosEncontrados = new HashSet<>();
    private final int TOTAL_PISTAS = 5;
    private String nombreJugador;

    public JuegoQR() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_juego_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Recuperar nombre del jugador
        nombreJugador = "Aventurero";
        if (getArguments() != null) {
            nombreJugador = getArguments().getString("nombreJugador", "Aventurero");
        }

        // 2. Vincular vistas
        tvPista1 = view.findViewById(R.id.textViewPista1);
        tvPista2 = view.findViewById(R.id.textViewPista2);
        tvPista3 = view.findViewById(R.id.textViewPista3);
        tvPista4 = view.findViewById(R.id.textViewPista4);
        tvPista5 = view.findViewById(R.id.textViewPista5);
        btnSiguiente = view.findViewById(R.id.buttonSiguiente);

        // 3. Configurar el lector QR
        View.OnClickListener escanearListener = v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Busca y escanea la pista correspondiente");
            options.setBeepEnabled(true);
            options.setOrientationLocked(false);
            options.setCaptureActivity(CaptureActivityPortrait.class); // Descomenta si tienes orientación vertical forzada
            qrLauncher.launch(options);
        };

        tvPista1.setOnClickListener(escanearListener);
        tvPista2.setOnClickListener(escanearListener);
        tvPista3.setOnClickListener(escanearListener);
        tvPista4.setOnClickListener(escanearListener);
        tvPista5.setOnClickListener(escanearListener);

        // 4. Botón Siguiente (Inicialmente desactivado hasta encontrar 5 pistas)
        btnSiguiente.setOnClickListener(v -> finalizarNivelYPasar(view));
    }

    // --- LANZADOR DEL ESCÁNER ---
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            procesarCodigoQR(result.getContents());
        }
    });

    private void procesarCodigoQR(String codigo) {
        String codigoLimpio = codigo.trim().toLowerCase();

        // Palabras clave válidas
        Set<String> codigosValidos = new HashSet<>(Arrays.asList("agua", "extintor", "cocacola", "baños", "sillas"));

        if (!codigosValidos.contains(codigoLimpio)) {
            Toast.makeText(getContext(), "Este QR no es del juego (" + codigoLimpio + ")", Toast.LENGTH_SHORT).show();
            return;
        }

        if (codigosEncontrados.contains(codigoLimpio)) {
            Toast.makeText(getContext(), "¡Ya tienes esta pista!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si es válido y nuevo, consultamos al servidor
        obtenerPistaDelServidor(codigoLimpio);
    }

    private void obtenerPistaDelServidor(String codigoQR) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // --- AQUÍ USAMOS LA CONSTANTE ---
        String url = Constantes.URL_SERVIDOR + "/pista/" + codigoQR;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> actualizarInterfaz(codigoQR, response),
                error -> Toast.makeText(getContext(), "Error de conexión con servidor", Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    private void actualizarInterfaz(String codigo, String textoPista) {
        codigosEncontrados.add(codigo);

        TextView tvActual = null;
        switch (codigo) {
            case "agua": tvActual = tvPista1; break;
            case "extintor": tvActual = tvPista2; break;
            case "cocacola": tvActual = tvPista3; break;
            case "baños": tvActual = tvPista4; break;
            case "sillas": tvActual = tvPista5; break;
        }

        if (tvActual != null) {
            tvActual.setText("✅ " + textoPista);
            tvActual.setClickable(false); // Ya no se puede volver a escanear
            tvActual.setBackgroundColor(Color.parseColor("#C8E6C9")); // Verde éxito
        }

        Toast.makeText(getContext(), "¡Pista encontrada! (" + codigosEncontrados.size() + "/5)", Toast.LENGTH_SHORT).show();

        // Si encontramos todas, activamos el botón para ir al ZIP
        if (codigosEncontrados.size() == TOTAL_PISTAS) {
            btnSiguiente.setEnabled(true);
            btnSiguiente.setText("IR AL MOTOR ZIP >>");
            btnSiguiente.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1B5E20")));
        }
    }

    private void finalizarNivelYPasar(View view) {
        // Notificar al servidor que acabamos este nivel
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // --- AQUÍ USAMOS LA CONSTANTE TAMBIÉN ---
        String url = Constantes.URL_SERVIDOR + "/finalizar/" + nombreJugador;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(getContext(), "¡Nivel QR Completado!", Toast.LENGTH_SHORT).show();
                    navegarAlSiguiente(view);
                },
                error -> {
                    // Si falla el servidor, pasamos igualmente para no bloquear al usuario
                    Toast.makeText(getContext(), "Error al guardar progreso, pero continuamos...", Toast.LENGTH_SHORT).show();
                    navegarAlSiguiente(view);
                }
        );
        queue.add(postRequest);
    }

    private void navegarAlSiguiente(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("nombreJugador", nombreJugador);

        // Ahora vamos al diálogo, no al zip directo
        Navigation.findNavController(view).navigate(R.id.action_juegoQR_to_dialogo_pre_zip, bundle);
    }
}