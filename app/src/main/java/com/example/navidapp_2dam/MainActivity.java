package com.example.navidapp_2dam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
            int currentId = navController.getCurrentDestination().getId();
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", "Admin (Cheater)");

            // Mapeamos cada pantalla con su destino (copiado del nav_graph)
            if (currentId == R.id.inicioHistoriaFragment) {
                navController.navigate(R.id.action_historia_to_juegoQR, bundle);
            }
            else if (currentId == R.id.juegoQRFragment) {
                navController.navigate(R.id.action_juegoQR_to_dialogoPreZip, bundle);
            }
            else if (currentId == R.id.dialogoPreZipFragment) {
                navController.navigate(R.id.action_dialogo_to_juegoZip, bundle);
            }
            else if (currentId == R.id.juegoZipFragment) {
                navController.navigate(R.id.action_juegoZip_to_descifrar, bundle);
            }
            else if (currentId == R.id.juegoDescifrarFragment) {
                navController.navigate(R.id.action_descifrar_to_dialogoHambre, bundle);
            }
            else if (currentId == R.id.dialogoPreSopaFragment) {
                navController.navigate(R.id.action_dialogoHambre_to_sopa, bundle);
            }
            else if (currentId == R.id.juegoSopaFragment) {
                navController.navigate(R.id.action_sopa_to_dialogoGrinch, bundle);
            }
            else if (currentId == R.id.dialogoPreQuizFragment) {
                navController.navigate(R.id.action_dialogoGrinch_to_quiz, bundle);
            }
            else if (currentId == R.id.juegoQuizFragment) {
                navController.navigate(R.id.action_quiz_to_final, bundle);
            }

        });
    }
}