package com.example.betweenlejavafx;

import javafx.scene.control.Button;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class SoundButton extends Button {

    public SoundButton(String texto) {
        super(texto);

        this.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-cursor: hand;");

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
