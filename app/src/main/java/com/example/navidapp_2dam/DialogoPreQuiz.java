package com.example.navidapp_2dam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class DialogoPreQuiz extends Fragment {

    private TextView tvSanta, tvGrinch;
    private ImageView imgSanta, imgGrinch;
    private Button btnDuelo;
    private String nombreJugador;
    private Handler handler = new Handler(Looper.getMainLooper());

    private String textoSanta = "SANTA: ¡Jo jo jo! ¡Delicioso! Gracias amigo. Ya tengo fuerzas para repartir los rega...";
    private String textoGrinch = "GRINCH: ¡JAJAJAJA! ¿A dónde vas gordinflón?\n¡Yo tengo los regalos!\nGáname en un DUELO si los quieres.";

    public DialogoPreQuiz() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialogo_pre_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) nombreJugador = getArguments().getString("nombreJugador", "Aventurero");

        tvSanta = view.findViewById(R.id.tvSantaDespedida);
        imgSanta = view.findViewById(R.id.imgSantaQuiz);

        tvGrinch = view.findViewById(R.id.tvGrinch);
        imgGrinch = view.findViewById(R.id.imgGrinchQuiz);

        btnDuelo = view.findViewById(R.id.btnDueloGrinch);

        iniciarSecuencia();

        btnDuelo.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);
            Navigation.findNavController(view).navigate(R.id.action_dialogoGrinch_to_quiz, bundle);
        });
    }

    private void iniciarSecuencia() {
        new Thread(() -> {
            try {
                // SANTA
                handler.post(() -> { imgSanta.setVisibility(View.VISIBLE); tvSanta.setVisibility(View.VISIBLE); });
                escribirTexto(tvSanta, textoSanta);
                Thread.sleep(textoSanta.length() * 40 + 1000);

                // GRINCH
                handler.post(() -> { imgGrinch.setVisibility(View.VISIBLE); tvGrinch.setVisibility(View.VISIBLE); });
                escribirTexto(tvGrinch, textoGrinch);
                Thread.sleep(textoGrinch.length() * 40 + 500);

                handler.post(() -> btnDuelo.setVisibility(View.VISIBLE));
            } catch (Exception e) {}
        }).start();
    }

    private void escribirTexto(TextView tv, String texto) {
        new Thread(() -> {
            for (int i = 0; i < texto.length(); i++) {
                int finalI = i;
                handler.post(() -> tv.append(String.valueOf(texto.charAt(finalI))));
                try { Thread.sleep(40); } catch (Exception e) {}
            }
        }).start();
    }
}