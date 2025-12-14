package com.example.navidapp_2dam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback; // IMPORTANTE
import androidx.appcompat.app.AlertDialog; // IMPORTANTE
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSkip = findViewById(R.id.btnGlobalSkip);

        // Configurar navegación
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            setupSkipButtonLogic();
        }

        // --- NUEVO: CONTROL DEL BOTÓN ATRÁS ---
        // Esto intercepta cuando el usuario le da al botón "Atrás" del móvil
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // En lugar de salir o volver, mostramos el aviso
                mostrarDialogoSalida();
            }
        });
    }

    // --- NUEVO: EL POP-UP DE ALERTA ---
    private void mostrarDialogoSalida() {
        new AlertDialog.Builder(this)
                .setTitle("🎄 ¿Abandonar la actividad? 🎄")
                .setMessage("⚠️ ¡CUIDADO!\n\nSi sales de la aplicación ahora, PERDERÁS todo tu progreso y tendrás que empezar de cero.\n\n¿Seguro que quieres salir?")
                .setPositiveButton("SÍ, SALIR", (dialog, which) -> {
                    finish(); // Cierra la aplicación completamente
                })
                .setNegativeButton("SEGUIR JUGANDO", (dialog, which) -> {
                    dialog.dismiss(); // Cierra el pop-up y no hace nada más
                })
                .setCancelable(false) // Obliga a pulsar uno de los botones
                .show();
    }

    private void setupSkipButtonLogic() {
        // 1. ESCUCHAMOS LOS CAMBIOS DE PANTALLA
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Solo mostramos el botón si es ADMIN y NO estamos en Login ni en Final
            if (Constantes.IS_ADMIN
                    && destination.getId() != R.id.loginFragment
                    && destination.getId() != R.id.juegoFinalFragment) {
                btnSkip.setVisibility(View.VISIBLE);
            } else {
                btnSkip.setVisibility(View.GONE);
            }
        });

        // 2. LÓGICA DEL BOTÓN (Mapa de saltos)
        btnSkip.setOnClickListener(v -> {
            try {
                int currentId = navController.getCurrentDestination().getId();
                // Usamos la constante global para el nombre

                // Mapeamos cada pantalla con su destino
                if (currentId == R.id.inicioHistoriaFragment) {
                    navController.navigate(R.id.action_historia_to_juegoQR);
                }
                else if (currentId == R.id.juegoQRFragment) {
                    navController.navigate(R.id.action_juegoQR_to_dialogoPreZip);
                }
                else if (currentId == R.id.dialogoPreZipFragment) {
                    navController.navigate(R.id.action_dialogo_to_juegoZip);
                }
                else if (currentId == R.id.juegoZipFragment) {
                    navController.navigate(R.id.action_juegoZip_to_descifrar);
                }
                else if (currentId == R.id.juegoDescifrarFragment) {
                    navController.navigate(R.id.action_descifrar_to_dialogoHambre);
                }
                else if (currentId == R.id.dialogoPreSopaFragment) {
                    navController.navigate(R.id.action_dialogoHambre_to_sopa);
                }
                else if (currentId == R.id.juegoSopaFragment) {
                    navController.navigate(R.id.action_sopa_to_dialogoGrinch);
                }
                else if (currentId == R.id.dialogoPreQuizFragment) {
                    navController.navigate(R.id.action_dialogoGrinch_to_quiz);
                }
                else if (currentId == R.id.juegoQuizFragment) {
                    navController.navigate(R.id.action_quiz_to_final);
                }
            } catch (Exception e) {
                e.printStackTrace();
                android.widget.Toast.makeText(this, "Error al saltar: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }
}