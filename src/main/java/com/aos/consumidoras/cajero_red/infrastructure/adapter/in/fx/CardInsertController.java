package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CardInsertController
{
    private static final Logger logger = LoggerFactory.getLogger(CardInsertController.class);

    private final AuthPort authPort;
    private final ApplicationContext applicationContext;

    @FXML
    private Button btnSimularInsercion;

    @Autowired
    public CardInsertController(AuthPort authPort, ApplicationContext applicationContext)
    {
        this.authPort = Objects.requireNonNull(authPort, "AuthPort no puede ser nulo");
        this.applicationContext = Objects.requireNonNull(applicationContext, "ApplicationContext no puede ser nulo");
    }

    @FXML
    public void handleInsertCard(ActionEvent event)
    {
        try
        {
            abrirPantallaAutenticacion(event);
        } catch (Exception e) {
            logger.error("Error en login automático, redirigiendo a autenticación manual", e);
            abrirPantallaAutenticacion(event);
        }
    }

    private void abrirPantallaAutenticacion(Event event)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/screens/AuthCard.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("Presiona ESC para salir de pantalla completa");
            stage.show();
        }
        catch (Exception e)
        {
            logger.error("Error al abrir la pantalla de autenticación", e);
        }
    }

    private void abrirMenuPrincipal(ActionEvent event) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/screens/Main.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Scene scene = new Scene(loader.load(), 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/views/styles.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Cajero RED - Menú");
    }
}