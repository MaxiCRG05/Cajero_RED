package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransferenciaResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.RealizarTransferenciaUseCase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

@Component
public class TransferController extends BaseController
{
    @FXML private TextField clabeField;
    @FXML private TextField beneficiarioField;
    @FXML private TextField amountField;
    @FXML private TextField conceptoField;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private SideNavBarController sideNavController;

    @Autowired private RealizarTransferenciaUseCase transferenciaService;
    @Autowired private SceneManager sceneManager;

    @FXML
    public void initialize()
    {
        addSmoothScaleHover(cancelButton, confirmButton);
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty())
                return null;

            String textWithoutDollar = newText.startsWith("$") ? newText.substring(1) : newText;

            if (!textWithoutDollar.matches("\\d*\\.?\\d*"))
                return null;

            if (!change.getControlNewText().startsWith("$")) {
                change.setText("$" + change.getText());
            }
            return change;
        };

        amountField.setTextFormatter(new TextFormatter<>(filter));
        amountField.setText("$0.00");
        sideNavController.setActiveButtonById("transferButton");
    }

    @FXML
    private void handleCancel()
    {
        goToMainMenu();
    }

    @FXML
    private void handleConfirm()
    {
        String clabe = clabeField.getText();
        if (clabe == null || clabe.trim().isEmpty())
        {
            showAlert("Error", "La CLABE destino es obligatoria.");
            return;
        }
        if (clabe.length() != 18)
        {
            showAlert("Error", "La CLABE debe tener exactamente 18 dígitos.");
            return;
        }
        if (!clabe.matches("\\d{18}"))
        {
            showAlert("Error", "La CLABE solo debe contener números.");
            return;
        }

        String montoStr = amountField.getText().replace("$", "").trim();
        BigDecimal monto;
        try
        {
            monto = new BigDecimal(montoStr);
        }
        catch (NumberFormatException e)
        {
            showAlert("Error", "El monto debe ser un número válido.");
            return;
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0)
        {
            showAlert("Error", "El monto debe ser mayor a cero.");
            return;
        }

        String token = SessionManager.getInstance().getToken();
        Integer usuarioId = SessionManager.getInstance().getUsuarioId();
        if (token == null || usuarioId == null)
        {
            showAlert("Error", "No hay sesión activa. Por favor, inicie sesión nuevamente.");
            return;
        }
        Long cuentaOrigenId = usuarioId.longValue();

        confirmButton.setDisable(true);
        cancelButton.setDisable(true);

        try
        {
            TransferenciaResponse response = transferenciaService.transferir(
                    cuentaOrigenId,
                    clabe,
                    new Monto(monto, "MXN"),
                    conceptoField.getText(),
                    token
            );

            String mensaje = String.format("ID transferencia: %d\nNuevo saldo origen: %.2f %s",
                    response.getTransferenciaId(),
                    response.getSaldoOrigenNuevo().getCantidad(),
                    response.getSaldoOrigenNuevo().getMoneda());
            showAlert("Transferencia exitosa", mensaje);
            goToMainMenu();

        }
        catch (HttpClientErrorException e)
        {
            String mensaje = extraerMensajeError(e);
            showAlert("Error en la transferencia", mensaje);
        }
        catch (HttpServerErrorException e)
        {
            String mensaje = extraerMensajeError(e);
            showAlert("Error del servidor", "Ocurrió un problema en el servidor. Por favor, intente más tarde.\n" + mensaje);
        }
        catch (ResourceAccessException e)
        {
            showAlert("Error de conexión", "No se pudo conectar con el servidor. Verifique su conexión a Internet.");
        }
        catch (Exception e)
        {
            showAlert("Error inesperado", "Ha ocurrido un error inesperado: " + e.getMessage());
        }
        finally
        {
            confirmButton.setDisable(false);
            cancelButton.setDisable(false);
        }
    }

    private String extraerMensajeError(HttpStatusCodeException e)
    {
        try
        {
            if (e.getResponseBodyAsString() != null && !e.getResponseBodyAsString().isEmpty())
            {
                String body = e.getResponseBodyAsString();
                int idxMensaje = body.indexOf("\"mensaje\":\"");
                if (idxMensaje == -1)
                    idxMensaje = body.indexOf("\"detail\":\"");
                if (idxMensaje != -1)
                {
                    int inicio = body.indexOf("\"", idxMensaje + 10) + 1;
                    int fin = body.indexOf("\"", inicio);
                    if (fin > inicio)
                        return body.substring(inicio, fin);
                }
                if (body.length() > 200)
                    body = body.substring(0, 200) + "...";
                return "Detalle: " + body;
            }
            return e.getMessage();
        }
        catch (Exception ex)
        {
            return "Error desconocido: " + e.getMessage();
        }
    }

    private void goToMainMenu()
    {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        sceneManager.cambiarEscena(stage, "/views/screens/Main.fxml");
    }

    private void showAlert(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}