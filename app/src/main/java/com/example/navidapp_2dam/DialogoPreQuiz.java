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

public class DialogoPreQuiz extends Fragment {

    private TextView tvSanta, tvGrinch;
    private Button btnDuelo;
    private String nombreJugador;

    // GUIÓN
    private String textoSanta = "SANTA: ¡Jo jo jo! ¡Delicioso! Gracias por la comida, amigo. Ya tengo fuerzas para retomar mi ruta y repartir los rega...";

    private String textoGrinch = "GRINCH: ¡JAJAJAJA! ¿A dónde crees que vas, gordinflón?\n\n" +
            "¡No tienes los regalos porque LOS TENGO YO!\n\n" +
            "Si quieres recuperarlos, tendrás que ganarme en un DUELO DE SABIDURÍA.";

    private Handler handler = new Handler(Looper.getMainLooper());

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
        tvGrinch = view.findViewById(R.id.tvGrinch);
        btnDuelo = view.findViewById(R.id.btnDueloGrinch);

        tvSanta.setText("");
        tvGrinch.setText("");

        iniciarSecuencia();

        btnDuelo.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);
            // Vamos al Quiz
            Navigation.findNavController(view).navigate(R.id.action_dialogoGrinch_to_quiz, bundle);
        });
    }

    private void iniciarSecuencia() {
        new Thread(() -> {
            // 1. Santa habla feliz
            for (int i = 0; i < textoSanta.length(); i++) {
                int finalI = i;
                handler.post(() -> tvSanta.append(String.valueOf(textoSanta.charAt(finalI))));
                try { Thread.sleep(40); } catch (InterruptedException e) {}
            }

            try { Thread.sleep(500); } catch (InterruptedException e) {}

            // 2. Aparece el Grinch de golpe
            handler.post(() -> tvGrinch.setVisibility(View.VISIBLE));

            // 3. Grinch habla
            for (int i = 0; i < textoGrinch.length(); i++) {
                int finalI = i;
                handler.post(() -> tvGrinch.append(String.valueOf(textoGrinch.charAt(finalI))));
                try { Thread.sleep(35); } catch (InterruptedException e) {}
            }

            // 4. Botón
            handler.post(() -> btnDuelo.setVisibility(View.VISIBLE));

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}