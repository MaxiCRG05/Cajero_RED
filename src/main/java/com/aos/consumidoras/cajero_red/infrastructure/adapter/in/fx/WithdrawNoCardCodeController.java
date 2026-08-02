package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;
import com.aos.consumidoras.cajero_red.domain.model.dto.ValidarCodigoRetiroResponse;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WithdrawNoCardCodeController extends BaseController
{
    @FXML private Text codigoDisplay;
    @FXML private TextField hiddenCodeField;
    @FXML private Label mensajeLabel;
    @FXML private Button btnCancelar;
    @FXML private Button btnVerificar;

    @Autowired private ESBPort esbPort;
    @Autowired private AuthPort authPort;
    @Autowired private SceneManager sceneManager;

    @Value("${atm.system.user.id}")
    private Integer systemUserId;

    private StringBuilder codigo = new StringBuilder();
    private static final int MAX_DIGITOS = 8;
    private boolean updating = false;

    @FXML
    public void initialize()
    {
        addSmoothScaleHover(btnCancelar, btnVerificar);
        mensajeLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        actualizarDisplay();
        hiddenCodeField.textProperty().addListener((obs, oldVal, newVal) ->
        {
            if (updating) return;
            mensajeLabel.setText("");
            if (newVal != null)
            {
                String soloDigitos = newVal.replaceAll("\\D", "");
                if (!soloDigitos.equals(codigo.toString()))
                {
                    codigo.setLength(0);
                    codigo.append(soloDigitos);
                    if (codigo.length() > MAX_DIGITOS)
                    {
                        codigo.setLength(MAX_DIGITOS);
                    }
                    actualizarDisplay();
                }
            }
        });
        hiddenCodeField.requestFocus();
    }

    private void actualizarDisplay()
    {
        updating = true;
        String asteriscos = "•".repeat(Math.min(codigo.length(), MAX_DIGITOS));
        String espacios = " ".repeat(Math.max(0, MAX_DIGITOS - codigo.length()));
        codigoDisplay.setText(asteriscos + espacios);
        hiddenCodeField.setText(codigo.toString());
        updating = false;
    }

    private void limpiarPantalla()
    {
        updating = true;
        codigo.setLength(0);
        actualizarDisplay();
        mensajeLabel.setText("");
        hiddenCodeField.requestFocus();
        updating = false;
    }

    private void limpiarCodigo()
    {
        updating = true;
        codigo.setLength(0);
        actualizarDisplay();
        hiddenCodeField.requestFocus();
        updating = false;
    }

    @FXML private void pressKey1() { agregarDigito('1'); }
    @FXML private void pressKey2() { agregarDigito('2'); }
    @FXML private void pressKey3() { agregarDigito('3'); }
    @FXML private void pressKey4() { agregarDigito('4'); }
    @FXML private void pressKey5() { agregarDigito('5'); }
    @FXML private void pressKey6() { agregarDigito('6'); }
    @FXML private void pressKey7() { agregarDigito('7'); }
    @FXML private void pressKey8() { agregarDigito('8'); }
    @FXML private void pressKey9() { agregarDigito('9'); }
    @FXML private void pressKey0() { agregarDigito('0'); }

    @FXML private void pressBackspace()
    {
        if (codigo.length() > 0)
        {
            codigo.deleteCharAt(codigo.length() - 1);
            actualizarDisplay();
        }
    }

    @FXML private void clearAll()
    {
        updating = true;
        codigo.setLength(0);
        actualizarDisplay();
        mensajeLabel.setText("");
        updating = false;
    }

    private void agregarDigito(char digito)
    {
        if (codigo.length() < MAX_DIGITOS)
        {
            codigo.append(digito);
            actualizarDisplay();
        }
    }

    @FXML
    public void handleVerificar(ActionEvent event)
    {
        String codigoStr = codigo.toString();

        if (codigoStr.isEmpty())
        {
            mensajeLabel.setText("Ingrese el código de 8 dígitos.");
            limpiarCodigo();
            return;
        }
        if (codigoStr.length() != MAX_DIGITOS)
        {
            mensajeLabel.setText("El código debe tener exactamente " + MAX_DIGITOS + " dígitos.");
            limpiarCodigo();
            return;
        }
        if (!codigoStr.matches("\\d{" + MAX_DIGITOS + "}"))
        {
            mensajeLabel.setText("El código solo debe contener números.");
            limpiarCodigo();
            return;
        }

        try
        {
            String token = SessionManager.getInstance().getToken();
            if (token == null)
            {
                TokenResponse tr = authPort.obtenerTokenPorUsuarioId(systemUserId);
                token = tr.getToken();
                if (token == null)
                {
                    mensajeLabel.setText("No se pudo obtener token de autenticación.");
                    limpiarCodigo();
                    return;
                }
            }

            ValidarCodigoRetiroResponse datos = esbPort.validarCodigoRetiro(codigoStr, token);

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar retiro sin tarjeta");
            confirm.setHeaderText("Retiro de $" + datos.getMonto() + " " + datos.getMoneda());
            confirm.setContentText("¿Confirma el retiro para " + datos.getNombres() + " " + datos.getApellidoPaterno() + "?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK)
            {
                TokenResponse tokenResponse = authPort.obtenerTokenPorUsuarioId(datos.getUsuarioId());
                String tokenEjecucion = tokenResponse.getToken();
                if (tokenEjecucion == null)
                {
                    mensajeLabel.setText("No se pudo obtener token para el usuario.");
                    limpiarCodigo();
                    return;
                }
                SessionManager.getInstance().setToken(tokenEjecucion);
                SessionManager.getInstance().setUsuarioId(datos.getUsuarioId());
                SessionManager.getInstance().setUsuarioNombre(
                        datos.getNombres() + " " + datos.getApellidoPaterno() +
                                (datos.getApellidoMaterno() != null ? " " + datos.getApellidoMaterno() : "")
                );
                SessionManager.getInstance().setClabe(tokenResponse.getClabe());

                esbPort.ejecutarRetiroSinTarjeta(datos.getSolicitudId(), tokenEjecucion);

                SessionManager.getInstance().setToken(null);
                SessionManager.getInstance().setUsuarioId(null);
                SessionManager.getInstance().setUsuarioNombre(null);
                SessionManager.getInstance().setClabe(null);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Retiro exitoso");
                success.setHeaderText(null);
                success.setContentText("Retiro sin tarjeta realizado exitosamente.");
                success.showAndWait();

                limpiarPantalla();
                Stage stage = (Stage) btnVerificar.getScene().getWindow();
                sceneManager.cambiarEscena(stage, "/views/screens/CardInsert.fxml");
            }
            else
                limpiarPantalla();
        }
        catch (Exception e)
        {
            String mensajeError = e.getMessage();
            if (mensajeError != null)
            {
                if (mensajeError.contains("403"))
                    mensajeLabel.setText("Token inválido o expirado. Por favor, intente nuevamente.");
                else if (mensajeError.contains("404"))
                    mensajeLabel.setText("Código no encontrado o expirado.");
                else if (mensajeError.contains("500"))
                    mensajeLabel.setText("Error interno del servidor. Intente más tarde.");
                else
                    mensajeLabel.setText("Error de conexión. Verifique su red e intente nuevamente.");
            }
            else
                mensajeLabel.setText("Error inesperado. Intente más tarde.");
            limpiarCodigo();
        }
    }

    @FXML
    public void handleCancel(ActionEvent event)
    {
        limpiarPantalla();
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