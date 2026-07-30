package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardInsertController
{
    private static final Logger logger = LoggerFactory.getLogger(CardInsertController.class);

    @FXML
    private Button btnSimularInsercion;

    @Autowired
    private AuthPort authPort;

    @Autowired
    private SceneManager sceneManager;

    @FXML
    public void handleInsertCard(ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) btnSimularInsercion.getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/AuthCard.fxml");
        }
        catch (Exception e)
        {
            logger.error("Error al abrir la pantalla de autenticación", e);
        }
    }
}