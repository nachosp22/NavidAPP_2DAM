package com.example.navidapp_2dam;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginFragment extends Fragment {

    EditText etNombre;
    Button btnIniciar;
    Button btnAdmin;

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNombre = view.findViewById(R.id.etNombre);
        btnIniciar = view.findViewById(R.id.btnIniciar);
        btnAdmin = view.findViewById(R.id.btnAdminBypass);

        // 1. BOTÓN INICIAR
        btnIniciar.setOnClickListener(v -> {
            String nombreInput = etNombre.getText().toString().trim();

            if (!nombreInput.isEmpty()) {
                // A. GUARDAMOS EL NOMBRE EN LA CONSTANTE GLOBAL
                Constantes.NOMBRE_JUGADOR = nombreInput;

                // B. AVISAMOS AL SERVIDOR (Para que empiece a contar el tiempo)
                iniciarRelojServidor();
            } else {
                Toast.makeText(getContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. CÓDIGO SECRETO ADMIN
        etNombre.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("adminadmin")) btnAdmin.setVisibility(View.VISIBLE);
                else btnAdmin.setVisibility(View.GONE);
            }
            public void afterTextChanged(Editable s) {}
        });

        // 3. BOTÓN ADMIN
        btnAdmin.setOnClickListener(v -> {
            Constantes.IS_ADMIN = true;
            Constantes.NOMBRE_JUGADOR = "Admin";
            Toast.makeText(getContext(), "⚡ MODO DIOS ACTIVADO ⚡", Toast.LENGTH_SHORT).show();
            // Los admin saltan sin petición al servidor
            Navigation.findNavController(view).navigate(R.id.action_login_to_inicioHistoria);
        });
    }

    private void iniciarRelojServidor() {
        btnIniciar.setEnabled(false);
        btnIniciar.setText("Conectando...");

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        // Usamos la constante directamente
        String url = Constantes.URL_SERVIDOR + "/crear/" + Constantes.NOMBRE_JUGADOR;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Éxito: El servidor ha arrancado el cronómetro
                    navegarAlJuego();
                },
                error -> {
                    // Fallo: Seguimos igual (modo offline)
                    Toast.makeText(getContext(), "Sin conexión (Jugando Offline)", Toast.LENGTH_SHORT).show();
                    navegarAlJuego();
                });

        queue.add(stringRequest);
    }

    private void navegarAlJuego() {
        if (getView() != null) {
            // Ya no hace falta pasar Bundle, el nombre está en Constantes
            Navigation.findNavController(getView()).navigate(R.id.action_login_to_inicioHistoria);
        }
    }
}