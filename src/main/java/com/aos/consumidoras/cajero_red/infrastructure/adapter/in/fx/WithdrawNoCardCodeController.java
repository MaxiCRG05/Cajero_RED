package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.domain.services.RetiroSinTarjetaService;
import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;
import com.aos.consumidoras.cajero_red.domain.model.dto.UsuarioDTO;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WithdrawNoCardCodeController extends BaseController
{
    @FXML private TextField codeField;
    @FXML private Label mensajeLabel;
    @FXML private Button btnCancelar;
    @FXML private Button btnVerificar;

    @Autowired private RetiroSinTarjetaService retiroSinTarjetaService;
    @Autowired private AuthPort authPort;
    @Autowired private SceneManager sceneManager;

    private String telefono;

    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    @FXML
    public void initialize()
    {
        addSmoothScaleHover(btnCancelar, btnVerificar);
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

        UsuarioDTO usuario = retiroSinTarjetaService.validarYRecuperarUsuario(telefono, codigo);
        if (usuario == null)
        {
            mensajeLabel.setText("Código incorrecto o expirado.");
            return;
        }

        try
        {
            TokenResponse tokenResponse = authPort.obtenerTokenPorUsuarioId(usuario.getId());
            SessionManager.getInstance().setTokenResponse(tokenResponse);
            SessionManager.getInstance().setUsuarioNombre(
                    usuario.getNombres() + " " + usuario.getApellidoPaterno() +
                            (usuario.getApellidoMaterno() != null ? " " + usuario.getApellidoMaterno() : "")
            );
            retiroSinTarjetaService.limpiarCodigo(telefono);
        }
        catch (Exception e)
        {
            mensajeLabel.setText("Error al obtener token de acceso.");
            return;
        }

        try
        {
            Stage stage = (Stage) btnVerificar.getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/WithdrawNoCardMain.fxml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            mensajeLabel.setText("Error al abrir la pantalla de retiro.");
        }
    }

    @FXML
    public void handleCancel(ActionEvent event)
    {
        if (telefono != null)
            retiroSinTarjetaService.limpiarCodigo(telefono);
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