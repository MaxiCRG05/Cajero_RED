package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AuthCardController
{
    private static final Logger logger = LoggerFactory.getLogger(AuthCardController.class);

    @FXML private TextField cardNumberField;
    @FXML private PasswordField nipField;
    @FXML private Label mensajeLabel;

    @Autowired
    private AuthPort authPort;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void handleValidate(ActionEvent event)
    {
        try
        {
            String numeroTarjeta = cardNumberField.getText();
            String nip = nipField.getText();
            TokenResponse tokenResponse = authPort.loginTarjeta(numeroTarjeta, nip);
            SessionManager.getInstance().setTokenResponse(tokenResponse);
            SessionManager.getInstance().setUsuarioNombre("Juan Pérez");
            abrirMenuPrincipal(event);
        }
        catch (Exception e)
        {
            logger.error("Error al validar la tarjeta", e);
            if (mensajeLabel != null)
            {
                mensajeLabel.setText("Datos incorrectos o error de conexión.");
            }
        }
    }

    @FXML
    public void handleCancel(ActionEvent event)
    {
        volverAInsertarTarjeta(event);
    }

    private void volverAInsertarTarjeta(ActionEvent event)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/screens/CardInsert.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/views/styles.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cajero RED - Inicio");
        }
        catch (Exception e)
        {
            logger.error("Error al regresar a la pantalla anterior", e);
        }
    }

    private void abrirMenuPrincipal(ActionEvent event) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/screens/Main.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Scene scene = new Scene(loader.load(), 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/views/styles.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Cajero RED - Menú");
    }
}