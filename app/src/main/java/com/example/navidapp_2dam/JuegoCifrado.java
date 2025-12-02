package com.example.navidapp_2dam;
public class JuegoCifrado {
    private static final int DESPLAZAMIENTO = 4;
    public String cifrarPalabra(String texto) {
        StringBuilder resultado = new StringBuilder();
        texto = texto.toUpperCase();
        for (char c : texto.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                char nuevaLetra = (char) ((c - 'A' + DESPLAZAMIENTO) % 26 + 'A');
                resultado.append(nuevaLetra);
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }
    public String descifrarPalabra(String textoCifrado) {
        StringBuilder resultado = new StringBuilder();
        textoCifrado = textoCifrado.toUpperCase();

        for (char c : textoCifrado.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                char nuevaLetra = (char) ((c - 'A' - DESPLAZAMIENTO + 26) % 26 + 'A');
                resultado.append(nuevaLetra);
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }
}
