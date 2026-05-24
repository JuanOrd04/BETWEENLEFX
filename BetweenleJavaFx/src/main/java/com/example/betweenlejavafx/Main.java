package com.example.betweenlejavafx;

import com.example.betweenlejavafx.model.BetweenleAPI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private BetweenleAPI api = new BetweenleAPI();
    private String idiomaSeleccionado = "ES";

    @Override
    public void start(Stage primaryStage) {
        mostrarPantallaIdioma(primaryStage);
    }

    private void mostrarPantallaIdioma(Stage stage) {
        Label lblTitulo = new Label("BETWEENLE");
        lblTitulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        Label lblPregunta = new Label("Selecciona tu idioma / Select your language:");

        ShapeButton btnEspanol = new ShapeButton("ESPAÑOL");
        ShapeButton btnIngles = new ShapeButton("INGLES");

        btnEspanol.setOnAction(e -> {
            idiomaSeleccionado = "ES";
            mostrarPantallaMenu(stage);
        });

        btnIngles.setOnAction(e -> {
            idiomaSeleccionado = "EN";
            mostrarPantallaMenu(stage);
        });

        HBox cajaBotones = new HBox(20, btnEspanol, btnIngles);
        cajaBotones.setAlignment(Pos.CENTER);

        VBox layout = new VBox(30, lblTitulo, lblPregunta, cajaBotones);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Scene escenaIdioma = new Scene(layout, 500, 400);
        stage.setTitle("Betweenle");
        stage.setScene(escenaIdioma);
        stage.show();
    }

    private void mostrarPantallaMenu(Stage stage) {
        boolean esEspanol = idiomaSeleccionado.equals("ES");

        String textoSubtitulo = esEspanol ? "Encuentra la palabra secreta entre los límites alfabéticos." : "Find the secret word between the alphabetical limits.";
        String textoLongitud = esEspanol ? "Longitud de palabra:" : "Word length:";
        String textoIntentos = esEspanol ? "Número de intentos:" : "Number of attempts:";
        String textoBoton = esEspanol ? "Iniciar Juego" : "Start Game";
        String textoExito = esEspanol ? "¡Juego preparado!" : "Game ready!";
        String textoErrorArchivo = esEspanol ? "Error: No se encontró el archivo " : "Error: File not found ";

        Label lblTitulo = new Label("BETWEENLE");
        lblTitulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Label lblSubtitulo = new Label(textoSubtitulo);

        Label lblLongitud = new Label(textoLongitud);
        Spinner<Integer> spinnerLongitud = new Spinner<>(5, 7, 5);
        spinnerLongitud.valueProperty().addListener((observador, valorViejo, valorNuevo) -> {
            reproducirSonido("/click.wav");
        });
        HBox boxLongitud = new HBox(10, lblLongitud, spinnerLongitud);
        boxLongitud.setAlignment(Pos.CENTER);

        Label lblIntentos = new Label(textoIntentos);
        Spinner<Integer> spinnerIntentos = new Spinner<>(10, 14, 10);
        spinnerIntentos.valueProperty().addListener((observador, valorViejo, valorNuevo) -> {
            reproducirSonido("/click.wav");
        });
        HBox boxIntentos = new HBox(10, lblIntentos, spinnerIntentos);
        boxIntentos.setAlignment(Pos.CENTER);

        Label lblEstado = new Label("");
        lblEstado.setVisible(false);

        SoundButton btnIniciar = new SoundButton(textoBoton);

        btnIniciar.setOnAction(e -> {
            int longitud = spinnerLongitud.getValue();
            int intentos = spinnerIntentos.getValue();
            String archivo = esEspanol ? "palabrasSpanol.txt" : "palabrasIngles.txt";

            try {
                api.cargarDiccionario(archivo);
                api.iniciarJuego(longitud, intentos);

                System.out.println("Palabra secreta: " + api.getSecreta());
                lblEstado.setText(textoExito);
                lblEstado.setStyle("-fx-text-fill: green;");
                lblEstado.setVisible(true);

            } catch (IOException ex) {
                lblEstado.setText(textoErrorArchivo + archivo);
                lblEstado.setStyle("-fx-text-fill: red;");
                lblEstado.setVisible(true);
            } catch (RuntimeException ex) {
                lblEstado.setText(ex.getMessage());
                lblEstado.setStyle("-fx-text-fill: red;");
                lblEstado.setVisible(true);
            }
        });

        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(40));
        menuLayout.getChildren().addAll(lblTitulo, lblSubtitulo, boxLongitud, boxIntentos, btnIniciar, lblEstado);

        Scene menuScene = new Scene(menuLayout, 500, 400);
        stage.setScene(menuScene);
    }

    private void reproducirSonido(String nombreArchivo) {
        try {
            java.io.InputStream audioSrc = getClass().getResourceAsStream(nombreArchivo);
            if (audioSrc != null) {
                java.io.InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
                javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(bufferedIn);
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (Exception ex) {
            System.out.println("Error al reproducir " + nombreArchivo + ": " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}