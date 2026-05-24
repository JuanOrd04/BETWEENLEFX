package com.example.betweenlejavafx;

import javafx.scene.control.Button;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class ShapeButton extends Button {

    public ShapeButton(String texto) {
        super(texto);

        String estiloNormal = "-fx-background-radius: 50em; -fx-min-width: 80px; -fx-min-height: 80px; -fx-max-width: 80px; -fx-max-height: 80px; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 16px;";

        this.setStyle(estiloNormal);

        this.setOnMousePressed(e -> {
            reproducirSonido("/click.wav");
        });
    }

    private void reproducirSonido(String nombreArchivo) {
        try {
            URL url = getClass().getResource(nombreArchivo);
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } else {
                System.out.println("No se encontró el sonido: " + nombreArchivo);
            }
        } catch (Exception ex) {
            System.out.println("Error al reproducir " + nombreArchivo + ": " + ex.getMessage());
        }
    }
}