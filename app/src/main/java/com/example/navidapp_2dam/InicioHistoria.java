package com.example.navidapp_2dam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class InicioHistoria extends Fragment {

    private String historia = "¡OH NO, DIOS MIO!\n SANTA CLAUS ACABA DE TENER UN ACCIDENTE, NECESITA AYUDA PARA PODER REPARTIR TODOS LOS REGALOS ESTE PROXIMO DIA 25\n ¿ESTAS DISPUESTO A AYUDARLE?";

    private TextView tvHistoria;
    private Button btnAyudar;
    private int indice = 0;
    private long velocidad = 50;
    private Handler handler = new Handler(Looper.getMainLooper());

    public InicioHistoria() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inicio_historia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- CÓDIGO DE BYPASS ELIMINADO ---

        tvHistoria = view.findViewById(R.id.tvHistoria);
        btnAyudar = view.findViewById(R.id.btnAyudarSanta);

        tvHistoria.setText("");
        btnAyudar.setVisibility(View.INVISIBLE);

        empezarAnimacion();

        btnAyudar.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_historia_to_juegoQR);
        });
    }

    private void empezarAnimacion() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (indice < historia.length()) {
                    tvHistoria.append(String.valueOf(historia.charAt(indice)));
                    indice++;
                    handler.postDelayed(this, velocidad);
                } else {
                    btnAyudar.setVisibility(View.VISIBLE);
                }
            }
        }, 500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}