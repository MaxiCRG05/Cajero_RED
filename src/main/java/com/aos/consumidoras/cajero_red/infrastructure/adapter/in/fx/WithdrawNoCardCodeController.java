package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Random;

@Component
public class WithdrawNoCardCodeController extends BaseController
{
    @FXML private TextField codeField;
    @FXML private Label mensajeLabel;
    @FXML private Button btnCancelar;
    @FXML private Button btnGenerarPrueba;
    @FXML private Button btnVerificar;

    @Autowired private ESBPort esbPort;
    @Autowired private AuthPort authPort;
    @Autowired private SceneManager sceneManager;

    private String codigoGenerado;
    private Integer usuarioIdPrueba;

    @FXML
    public void initialize()
    {
        addSmoothScaleHover(btnCancelar, btnGenerarPrueba, btnVerificar);
    }

    @FXML
    public void handleGenerarPrueba(ActionEvent event)
    {
        usuarioIdPrueba = 1;
        codigoGenerado = String.format("%08d", new Random().nextInt(100000000));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Código de prueba generado");
        alert.setHeaderText("Código asociado al usuario ID: " + usuarioIdPrueba);
        alert.setContentText("Código: " + codigoGenerado + "\nIngréselo en el campo y presione Verificar.");
        alert.showAndWait();
    }

    @FXML
    public void handleVerificar(ActionEvent event)
    {
        String codigo = codeField.getText().trim();
        if (codigo.isEmpty())
        {
            mensajeLabel.setText("Ingrese el código de 8 dígitos.");
            return;
        }
        if (!codigo.matches("\\d{8}"))
        {
            mensajeLabel.setText("El código debe tener 8 dígitos numéricos.");
            return;
        }

        if (codigoGenerado != null && codigoGenerado.equals(codigo) && usuarioIdPrueba != null)
        {
            try
            {
                TokenResponse tokenResponse = authPort.obtenerTokenPorUsuarioId(usuarioIdPrueba);
                SessionManager.getInstance().setToken(tokenResponse.getToken());
                SessionManager.getInstance().setUsuarioId(usuarioIdPrueba);
                if (tokenResponse.getClabe() != null)
                {
                    SessionManager.getInstance().setClabe(tokenResponse.getClabe());
                }
                Stage stage = (Stage) btnVerificar.getScene().getWindow();
                sceneManager.cambiarEscena(stage, "/views/screens/WithdrawNoCardMain.fxml");
                return;
            }
            catch (Exception e)
            {
                mensajeLabel.setText("Error al obtener token: " + e.getMessage());
                return;
            }
        }

        try
        {
            Map<String, Object> datos = esbPort.validarCodigoRetiro(codigo, null);
            Integer usuarioId = (Integer) datos.get("Id");
            String nombres = (String) datos.get("Nombres");
            String apellidoPaterno = (String) datos.get("ApellidoPaterno");
            String apellidoMaterno = (String) datos.get("ApellidoMaterno");
            String clabe = (String) datos.get("Clabe");

            if (usuarioId == null)
            {
                mensajeLabel.setText("Código inválido o expirado.");
                return;
            }

            TokenResponse tokenResponse = authPort.obtenerTokenPorUsuarioId(usuarioId);
            SessionManager.getInstance().setToken(tokenResponse.getToken());
            SessionManager.getInstance().setUsuarioId(usuarioId);
            String nombreCompleto = nombres + " " + apellidoPaterno;
            if (apellidoMaterno != null && !apellidoMaterno.isEmpty())
            {
                nombreCompleto += " " + apellidoMaterno;
            }
            SessionManager.getInstance().setUsuarioNombre(nombreCompleto);
            if (clabe != null && !clabe.isEmpty())
            {
                SessionManager.getInstance().setClabe(clabe);
            }
            else if (tokenResponse.getClabe() != null)
            {
                SessionManager.getInstance().setClabe(tokenResponse.getClabe());
            }

            Stage stage = (Stage) btnVerificar.getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/WithdrawNoCardMain.fxml");
        }
        catch (Exception e)
        {
            mensajeLabel.setText("Código inválido o error de conexión: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel(ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/CardInsert.fxml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}