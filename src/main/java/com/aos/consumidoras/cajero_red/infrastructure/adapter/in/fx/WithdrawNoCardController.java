package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.domain.model.dto.UsuarioDTO;
import com.aos.consumidoras.cajero_red.domain.services.RetiroSinTarjetaService;
import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WithdrawNoCardController extends BaseController
{
    @FXML private TextField phoneField;
    @FXML private Label mensajeLabel;
    @FXML private Button btnCancelar;
    @FXML private Button btnValidar;

    @Autowired private RetiroSinTarjetaService retiroSinTarjetaService;
    @Autowired private ESBPort esbPort;
    @Autowired private SceneManager sceneManager;
    @Autowired private ApplicationContext applicationContext;

    @FXML
    public void initialize()
    {
        addSmoothScaleHover(btnCancelar, btnValidar);
    }

    @FXML
    public void handleValidate(ActionEvent event)
    {
        String telefono = phoneField.getText().trim();

        if (!retiroSinTarjetaService.esTelefonoValido(telefono))
        {
            mensajeLabel.setText("Número inválido (10 dígitos requeridos).");
            return;
        }

        try
        {
            UsuarioDTO usuario = esbPort.obtenerUsuarioPorTelefono(telefono);
            retiroSinTarjetaService.guardarUsuario(telefono, usuario);
        }
        catch (Exception e)
        {
            mensajeLabel.setText("Usuario no encontrado o error en el servidor.");
            return;
        }

        String codigo = retiroSinTarjetaService.generarCodigo(telefono);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Código de Verificación");
        alert.setHeaderText("Código enviado a su teléfono (simulado)");
        alert.setContentText("Su código de 8 dígitos es: " + codigo + "\nVálido por 5 minutos.");
        alert.showAndWait();

        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/screens/WithdrawNoCardCode.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            WithdrawNoCardCodeController controller = loader.getController();
            controller.setTelefono(telefono);

            Stage stage = (Stage) btnValidar.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            mensajeLabel.setText("Error al abrir la pantalla de verificación.");
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