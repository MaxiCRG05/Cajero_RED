package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;
import com.aos.consumidoras.cajero_red.domain.model.dto.UsuarioDTO;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
public class AuthCardController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(AuthCardController.class);

    @FXML private TextField cardNumberField;
    @FXML private PasswordField nipField;
    @FXML private Label mensajeLabel;
    @FXML private Button btnCancelar;
    @FXML private Button btnValidar;

    @Autowired private AuthPort authPort;
    @Autowired private ESBPort esbPort;
    @Autowired private SceneManager sceneManager;

    @FXML
    public void initialize()
    {
        addSmoothScaleHover(btnCancelar, btnValidar);

        UnaryOperator<TextFormatter.Change> tarjetaFilter = change ->
        {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,16}"))
                return change;
            return null;
        };
        cardNumberField.setTextFormatter(new TextFormatter<>(tarjetaFilter));

        UnaryOperator<TextFormatter.Change> nipFilter = change ->
        {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,4}"))
                return change;
            return null;
        };
        nipField.setTextFormatter(new TextFormatter<>(nipFilter));
    }

    @FXML
    public void handleValidate(ActionEvent event)
    {
        String numeroTarjeta = cardNumberField.getText();
        String nip = nipField.getText();

        if (numeroTarjeta.length() != 16)
        {
            mensajeLabel.setText("El número de tarjeta debe tener 16 dígitos.");
            return;
        }
        if (nip.length() != 4)
        {
            mensajeLabel.setText("El NIP debe tener 4 dígitos.");
            return;
        }

        try
        {
            TokenResponse tokenResponse = authPort.loginTarjeta(numeroTarjeta, nip);
            SessionManager.getInstance().setTokenResponse(tokenResponse);

            UsuarioDTO usuario = esbPort.obtenerUsuario(tokenResponse.getUsuarioId(), tokenResponse.getToken());
            String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidoPaterno();
            if (usuario.getApellidoMaterno() != null && !usuario.getApellidoMaterno().isEmpty())
                nombreCompleto += " " + usuario.getApellidoMaterno();
            SessionManager.getInstance().setUsuarioNombre(nombreCompleto.trim());

            abrirMenuPrincipal(event);
        }
        catch (Exception e)
        {
            logger.error("Error al validar la tarjeta", e);
            mensajeLabel.setText("Datos incorrectos o error de conexión.");
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/CardInsert.fxml");
        }
        catch (Exception e)
        {
            logger.error("Error al regresar a la pantalla anterior", e);
        }
    }

    private void abrirMenuPrincipal(ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/Main.fxml");
        }
        catch (Exception e)
        {
            logger.error("Error al abrir el menú principal", e);
        }
    }
}