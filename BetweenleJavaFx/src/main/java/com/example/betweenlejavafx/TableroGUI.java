package com.example.betweenlejavafx;

import com.example.betweenlejavafx.model.BetweenleAPI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class TableroGUI {

    private static int indiceActual = 0;
    private static StringBuilder palabraActual = new StringBuilder();
    private static boolean pistaUsada = false;

    private static final Map<String, Button> mapaBotonesTeclado = new HashMap<>();

    private static final String ESTILO_NORMAL = "-fx-background-radius: 50em; -fx-min-width: 36px; -fx-min-height: 36px; -fx-max-width: 36px; -fx-max-height: 36px; -fx-background-color: #e5dbd5; -fx-text-fill: #8b796e; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand;";
    private static final String ESTILO_HOVER = "-fx-background-radius: 50em; -fx-min-width: 36px; -fx-min-height: 36px; -fx-max-width: 36px; -fx-max-height: 36px; -fx-background-color: #d1c5bf; -fx-text-fill: #6e5c53; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand;";
    private static final String ESTILO_GRIS = "-fx-background-radius: 50em; -fx-min-width: 36px; -fx-min-height: 36px; -fx-max-width: 36px; -fx-max-height: 36px; -fx-background-color: #dcdcdc; -fx-text-fill: #a9a9a9; -fx-font-weight: bold; -fx-font-size: 15px; -fx-opacity: 0.8; -fx-cursor: hand;";

    public static void mostrar(Stage stage, BetweenleAPI api, String idioma, Runnable accionVolver) {
        indiceActual = 0;
        palabraActual.setLength(0);
        pistaUsada = false;
        mapaBotonesTeclado.clear();

        boolean esEspanol = idioma.equals("ES");
        int longitud = api.getPalabraTop() != null ? api.getPalabraTop().length() : 5;

        String txtTop = esEspanol ? "Límite Superior:" : "Top Limit:";
        String txtBottom = esEspanol ? "Límite Inferior:" : "Bottom Limit:";
        String txtIntentos = esEspanol ? "Intentos restantes: " : "Attempts left: ";
        String txtGanaste = esEspanol ? "¡GANASTE! Felicidades." : "YOU WON! Congratulations.";
        String txtPerdiste = esEspanol ? "¡Perdiste! La palabra era: " : "You lost! The word was: ";
        String txtAtras = esEspanol ? "⬅ Rendirse y Volver" : "⬅ Give up and Return";

        SoundButton btnAtras = new SoundButton(txtAtras);
        btnAtras.setStyle("-fx-font-size: 14px; -fx-padding: 5 10; -fx-background-color: #c0392b; -fx-text-fill: white; -fx-cursor: hand;");
        btnAtras.setOnAction(e -> accionVolver.run());

        SoundButton btnPistas = new SoundButton(esEspanol ? "💡 Usar Pista" : "💡 Use Hint");
        btnPistas.setStyle("-fx-font-size: 14px; -fx-padding: 5 10; -fx-background-color: #2980b9; -fx-text-fill: white; -fx-cursor: hand;");

        HBox barraSuperior = new HBox(15, btnAtras, btnPistas);
        barraSuperior.setAlignment(Pos.TOP_LEFT);

        Label lblTop = new Label(formatearLimite(txtTop, api.getPalabraTop(), api.getPorcentajeTop()));
        lblTop.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblBottom = new Label(formatearLimite(txtBottom, api.getPalabraBottom(), api.getPorcentajeBottom()));
        lblBottom.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #c0392b;");

        Label lblIntentosDisp = new Label(txtIntentos + api.getIntentos());
        lblIntentosDisp.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox cajaLetras = new HBox(10);
        cajaLetras.setAlignment(Pos.CENTER);
        ImageButton[] casillas = new ImageButton[longitud];
        for (int i = 0; i < longitud; i++) {
            casillas[i] = new ImageButton();
            cajaLetras.getChildren().add(casillas[i]);
        }

        Label lblMensaje = new Label(esEspanol ? "Escribe o usa el teclado en pantalla" : "Type or use the on-screen keyboard");
        lblMensaje.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");

        ListView<String> listaHistorial = new ListView<>();
        listaHistorial.setMaxHeight(100);
        listaHistorial.setMaxWidth(300);

        VBox layoutTablero = new VBox(15);
        layoutTablero.setAlignment(Pos.CENTER);

        Consumer<String> accionEscribir = (letra) -> {
            if (indiceActual < longitud && api.getIntentos() > 0 && !lblMensaje.getText().equals(txtGanaste)) {
                palabraActual.append(letra.toLowerCase());
                casillas[indiceActual].setImagenLetra(letra.toLowerCase());
                indiceActual++;

                evaluarRangoAbecedario(api, longitud);
            }
        };

        Runnable accionBorrar = () -> {
            if (indiceActual > 0 && api.getIntentos() > 0 && !lblMensaje.getText().equals(txtGanaste)) {
                indiceActual--;
                palabraActual.deleteCharAt(indiceActual);
                casillas[indiceActual].setImagenLetra("?");
                lblMensaje.setText(esEspanol ? "Escribe o usa el teclado en pantalla" : "Type or use the on-screen keyboard");
                lblMensaje.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");

                evaluarRangoAbecedario(api, longitud);
            }
        };

        Runnable accionEnter = () -> {
            if (indiceActual != longitud || api.getIntentos() <= 0 || lblMensaje.getText().equals(txtGanaste)) return;

            String intento = palabraActual.toString();

            if (!api.esValida(intento)) {
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle(esEspanol ? "Palabra no encontrada" : "Word not found");
                alerta.setHeaderText(null);
                alerta.setContentText(esEspanol ?
                        "La palabra '" + intento.toUpperCase() + "' no existe.\n¿Es real? ¿Deseas agregarla?" :
                        "The word '" + intento.toUpperCase() + "' is not in the dictionary.\nIs it real? Do you want to add it?");

                ButtonType btnSi = new ButtonType(esEspanol ? "Sí" : "Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                alerta.getButtonTypes().setAll(btnSi, btnNo);

                alerta.showAndWait().ifPresent(respuesta -> {
                    if (respuesta == btnSi) {
                        TextInputDialog dialogo = new TextInputDialog(String.valueOf(longitud));
                        dialogo.setTitle(esEspanol ? "Agregar palabra" : "Add word");
                        dialogo.setHeaderText(null);
                        dialogo.setContentText(esEspanol ? "Longitud de la palabra:" : "Word length:");

                        dialogo.showAndWait().ifPresent(longitudIngresada -> {
                            api.agregarPalabra(intento, longitudIngresada);
                            lblMensaje.setText(esEspanol ? "¡Palabra guardada! Inténtalo de nuevo." : "Word saved! Try again.");
                            lblMensaje.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: green;");

                            indiceActual = 0;
                            palabraActual.setLength(0);
                            for (int i = 0; i < longitud; i++) casillas[i].setImagenLetra("?");
                            evaluarRangoAbecedario(api, longitud);
                        });
                    } else {
                        lblMensaje.setText(esEspanol ? "La palabra no está en el diccionario." : "Word not found in the dictionary.");
                        lblMensaje.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
                    }
                    layoutTablero.requestFocus();
                });
                return;
            }

            String resultado = api.procesarIntento(intento);

            if (resultado.equals("FUERA_DE_RANGO_TOP") || resultado.equals("FUERA_DE_RANGO_BOTTOM")) {
                Alert alertaError = new Alert(Alert.AlertType.ERROR);
                alertaError.setTitle(esEspanol ? "Error de Rango" : "Out of Range Error");
                alertaError.setHeaderText(null);

                if (resultado.equals("FUERA_DE_RANGO_TOP")) {
                    alertaError.setContentText(esEspanol ? "¡ERROR! Tu palabra va ANTES del límite superior actual." : "¡ERROR! Your word goes BEFORE the current top limit.");
                } else {
                    alertaError.setContentText(esEspanol ? "¡ERROR! Tu palabra va DESPUÉS del límite inferior actual." : "¡ERROR! Your word goes AFTER the current bottom limit.");
                }

                alertaError.showAndWait();
                layoutTablero.requestFocus();
                return;
            }

            lblTop.setText(formatearLimite(txtTop, api.getPalabraTop(), api.getPorcentajeTop()));
            lblBottom.setText(formatearLimite(txtBottom, api.getPalabraBottom(), api.getPorcentajeBottom()));
            lblIntentosDisp.setText(txtIntentos + api.getIntentos());

            listaHistorial.getItems().clear();
            listaHistorial.getItems().addAll(api.getHistorialPalabras());

            indiceActual = 0;
            palabraActual.setLength(0);
            for (int i = 0; i < longitud; i++) casillas[i].setImagenLetra("?");

            evaluarRangoAbecedario(api, longitud);

            if (resultado.equals("GANASTE")) {
                lblMensaje.setText(txtGanaste);
                lblMensaje.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green;");
                btnPistas.setDisable(true);
            } else if (api.getIntentos() <= 0) {
                lblMensaje.setText(txtPerdiste + api.getSecreta().toUpperCase());
                lblMensaje.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");
                btnPistas.setDisable(true);
            } else {
                lblMensaje.setText(resultado);
                lblMensaje.setStyle("-fx-font-size: 14px; -fx-text-fill: #d35400;");
            }
        };

        VBox tecladoVirtual = new VBox(8);
        tecladoVirtual.setAlignment(Pos.CENTER);

        HBox fila1 = new HBox(6);
        fila1.setAlignment(Pos.CENTER);
        String[] letrasFila1 = {"A","B","C","D","E","F","G","H","I","J","K","L","M"};
        for (String letra : letrasFila1) {
            Button btnLetra = crearBotonTeclado(letra);
            btnLetra.setOnAction(e -> accionEscribir.accept(letra));
            fila1.getChildren().add(btnLetra);
            mapaBotonesTeclado.put(letra, btnLetra);
        }

        HBox fila2 = new HBox(6);
        fila2.setAlignment(Pos.CENTER);
        String[] letrasFila2 = {"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        for (String letra : letrasFila2) {
            Button btnLetra = crearBotonTeclado(letra);
            btnLetra.setOnAction(e -> accionEscribir.accept(letra));
            fila2.getChildren().add(btnLetra);
            mapaBotonesTeclado.put(letra, btnLetra);
        }

        HBox filaControles = new HBox(20);
        filaControles.setAlignment(Pos.CENTER);

        Button btnEnterVirtual = new Button("ENTER ⏎");
        btnEnterVirtual.setStyle("-fx-background-radius: 20px; -fx-background-color: #d1c5bf; -fx-text-fill: #7d6b61; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20;");
        btnEnterVirtual.setFocusTraversable(false);
        btnEnterVirtual.setOnAction(e -> accionEnter.run());

        Button btnBorrarVirtual = new Button(esEspanol ? "⌫ BORRAR" : "⌫ DELETE");
        btnBorrarVirtual.setStyle("-fx-background-radius: 20px; -fx-background-color: #d1c5bf; -fx-text-fill: #7d6b61; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20;");
        btnBorrarVirtual.setFocusTraversable(false);
        btnBorrarVirtual.setOnAction(e -> accionBorrar.run());

        filaControles.getChildren().addAll(btnEnterVirtual, btnBorrarVirtual);
        tecladoVirtual.getChildren().addAll(fila1, fila2, filaControles);

        btnPistas.setOnAction(e -> {
            if (pistaUsada) return;

            Alert menuPistas = new Alert(Alert.AlertType.CONFIRMATION);
            menuPistas.setTitle(esEspanol ? "Menú de Pistas" : "Hint Menu");
            menuPistas.setHeaderText(esEspanol ? "¿Qué tipo de pista deseas usar?" : "What kind of hint do you want?");

            ButtonType btnA = new ButtonType(esEspanol ? "A) +1% Límite Superior" : "A) +1% Top Limit");
            ButtonType btnB = new ButtonType(esEspanol ? "B) -1% Límite Inferior" : "B) -1% Bottom Limit");
            ButtonType btnC = new ButtonType(esEspanol ? "C) 1ra Letra" : "C) 1st Letter");
            ButtonType btnCancelar = new ButtonType(esEspanol ? "Cancelar" : "Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            menuPistas.getButtonTypes().setAll(btnA, btnB, btnC, btnCancelar);

            Optional<ButtonType> opcionElegida = menuPistas.showAndWait();

            if (opcionElegida.isPresent() && opcionElegida.get() != btnCancelar) {
                String resultadoPista = "";
                ButtonType opcion = opcionElegida.get();

                if (opcion == btnA) {
                    if (!api.esValida(api.getPalabraTop())) {
                        resultadoPista = esEspanol ? "Error: Aún no tienes un Límite Superior real." : "Error: No real Top Limit yet.";
                    } else {
                        resultadoPista = api.darPistaTop1Porciento();
                        pistaUsada = true;
                    }
                } else if (opcion == btnB) {
                    if (!api.esValida(api.getPalabraBottom())) {
                        resultadoPista = esEspanol ? "Error: Aún no tienes un Límite Inferior real." : "Error: No real Bottom Limit yet.";
                    } else {
                        resultadoPista = api.darPistaBottom1Porciento();
                        pistaUsada = true;
                    }
                } else if (opcion == btnC) {
                    resultadoPista = api.obtenerPistaComienzo();
                    pistaUsada = true;
                }

                if (pistaUsada) {
                    lblTop.setText(formatearLimite(txtTop, api.getPalabraTop(), api.getPorcentajeTop()));
                    lblBottom.setText(formatearLimite(txtBottom, api.getPalabraBottom(), api.getPorcentajeBottom()));
                    btnPistas.setDisable(true);

                    evaluarRangoAbecedario(api, longitud);

                    Alert alertaExito = new Alert(Alert.AlertType.INFORMATION, resultadoPista);
                    alertaExito.setHeaderText(esEspanol ? "¡Pista Revelada!" : "Hint Revealed!");
                    alertaExito.showAndWait();
                } else {
                    Alert alertaError = new Alert(Alert.AlertType.ERROR, resultadoPista);
                    alertaError.showAndWait();
                }
            }
            layoutTablero.requestFocus();
        });

        layoutTablero.getChildren().addAll(
                lblIntentosDisp, lblTop, cajaLetras, lblBottom, tecladoVirtual, lblMensaje, listaHistorial
        );

        VBox contenedorPrincipal = new VBox(15);
        contenedorPrincipal.setPadding(new Insets(20));
        contenedorPrincipal.getChildren().addAll(barraSuperior, layoutTablero);

        Scene escenaJuego = new Scene(contenedorPrincipal, 650, 700);

        escenaJuego.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code.isLetterKey()) {
                accionEscribir.accept(code.getName());
            } else if (code == KeyCode.BACK_SPACE) {
                accionBorrar.run();
            } else if (code == KeyCode.ENTER) {
                accionEnter.run();
            }
        });

        evaluarRangoAbecedario(api, longitud);

        stage.setScene(escenaJuego);
        layoutTablero.requestFocus();
    }

    private static void evaluarRangoAbecedario(BetweenleAPI api, int longitud) {
        String top = api.getPalabraTop();
        String bottom = api.getPalabraBottom();
        String prefijoActual = palabraActual.toString().toLowerCase();

        if (prefijoActual.length() == longitud) {
            for (Button btn : mapaBotonesTeclado.values()) {
                btn.getProperties().put("rangoInvalido", true);
                btn.setStyle(ESTILO_GRIS);
            }
            return;
        }

        for (Map.Entry<String, Button> entrada : mapaBotonesTeclado.entrySet()) {
            String letraStr = entrada.getKey();
            Button btn = entrada.getValue();
            char letraCandidata = letraStr.toLowerCase().charAt(0);

            String combinacion = prefijoActual + letraCandidata;
            int faltantes = longitud - combinacion.length();

            StringBuilder maxSb = new StringBuilder(combinacion);
            StringBuilder minSb = new StringBuilder(combinacion);
            for (int i = 0; i < faltantes; i++) {
                maxSb.append('z');
                minSb.append('a');
            }
            String maxPalabra = maxSb.toString();
            String minPalabra = minSb.toString();

            boolean fueraPorArriba = top != null && !top.isEmpty() && maxPalabra.compareTo(top.toLowerCase().trim()) <= 0;
            boolean fueraPorAbajo = bottom != null && !bottom.isEmpty() && minPalabra.compareTo(bottom.toLowerCase().trim()) >= 0;

            if (fueraPorArriba || fueraPorAbajo) {
                btn.getProperties().put("rangoInvalido", true);
                btn.setStyle(ESTILO_GRIS);
            } else {
                btn.getProperties().put("rangoInvalido", false);
                btn.setStyle(ESTILO_NORMAL);
            }
        }
    }

    private static Button crearBotonTeclado(String letra) {
        Button btn = new Button(letra);
        btn.getProperties().put("rangoInvalido", false);
        btn.setStyle(ESTILO_NORMAL);
        btn.setFocusTraversable(false);

        btn.setOnMouseEntered(e -> {
            Boolean invalido = (Boolean) btn.getProperties().get("rangoInvalido");
            if (invalido != null && invalido) {
                btn.setStyle(ESTILO_GRIS);
            } else {
                btn.setStyle(ESTILO_HOVER);
            }
        });

        btn.setOnMouseExited(e -> {
            Boolean invalido = (Boolean) btn.getProperties().get("rangoInvalido");
            if (invalido != null && invalido) {
                btn.setStyle(ESTILO_GRIS);
            } else {
                btn.setStyle(ESTILO_NORMAL);
            }
        });

        return btn;
    }

    private static String formatearLimite(String textoBase, String palabra, double porcentaje) {
        if (palabra == null) return textoBase + " ---- (?)";
        if (porcentaje == -1) {
            return textoBase + " " + palabra.toUpperCase() + " (?)";
        } else {
            return String.format("%s %s (%.2f%%)", textoBase, palabra.toUpperCase(), porcentaje);
        }
    }
}