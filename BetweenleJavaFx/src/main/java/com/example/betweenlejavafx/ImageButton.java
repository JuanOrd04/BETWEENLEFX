package com.example.betweenlejavafx;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageButton extends Button {

    public ImageButton() {
        super("?"); // Llamamos al constructor padre con el signo de interrogación inicial

        // Configuramos el estilo para que parezca una casilla de juego (estilo Wordle)
        this.setStyle(
                "-fx-border-color: #34495e; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-size: 28px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-alignment: center; " +
                        "-fx-min-width: 90px; " +
                        "-fx-min-height: 80px; " +
                        "-fx-background-color: #ecf0f1;"
        );

        this.setFocusTraversable(false);
    }

    public void setImagenLetra(String letra) {
        if (letra.equals("?")) {
            this.setGraphic(null);
            this.setText("?");
            return;
        }

        try {
            String rutaImagen = "/letras/" + letra.toLowerCase() + ".png";
            Image imgLetra = new Image(getClass().getResourceAsStream(rutaImagen));
            ImageView imgView = new ImageView(imgLetra);
            imgView.setFitWidth(50);
            imgView.setFitHeight(50);

            this.setText("");
            this.setGraphic(imgView);
        } catch (Exception e) {
            this.setGraphic(null);
            this.setText(letra.toUpperCase());
        }
    }
}