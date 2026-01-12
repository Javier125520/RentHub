package org.example.renthub.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Ventanas {

    public static void abrirModal(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Ventanas.class.getResource(fxml)
            );

            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // 🔑 Ajusta el stage al contenido real
            stage.sizeToScene();

            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("ERROR abriendo modal: " + fxml);
            e.printStackTrace();
        }
    }


    // Abrir modal CON datos (Editar)
    public static <T> void abrirModalConDatos(String fxml, String titulo, T datos) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Ventanas.class.getResource(fxml)
            );
            Parent root = loader.load();

            Object controller = loader.getController();
            controller.getClass()
                    .getMethod("setDatos", datos.getClass())
                    .invoke(controller, datos);

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // 🔑 Ajusta el stage al contenido real
            stage.sizeToScene();

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


