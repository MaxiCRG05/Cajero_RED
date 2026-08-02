package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransaccionResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarSaldoUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.RealizarRetiroUseCase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

@Component
public class WithdrawController extends BaseController
{
    @FXML private TextField amountField;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private Text saldoLabel;
    @FXML private SideNavBarController sideNavController;

    @FXML private GridPane keypadGrid;

    @FXML private void pressKey1() { addDigit("1"); }
    @FXML private void pressKey2() { addDigit("2"); }
    @FXML private void pressKey3() { addDigit("3"); }
    @FXML private void pressKey4() { addDigit("4"); }
    @FXML private void pressKey5() { addDigit("5"); }
    @FXML private void pressKey6() { addDigit("6"); }
    @FXML private void pressKey7() { addDigit("7"); }
    @FXML private void pressKey8() { addDigit("8"); }
    @FXML private void pressKey9() { addDigit("9"); }
    @FXML private void pressKey0() { addDigit("0"); }

    @Autowired private RealizarRetiroUseCase retiroService;
    @Autowired private ConsultarSaldoUseCase consultarSaldo;
    @Autowired private SceneManager sceneManager;

    private BigDecimal saldoActual;

    @FXML
    public void initialize()
    {
        addSmoothScaleHover(cancelButton, confirmButton);
        if (keypadGrid != null)
        {
            keypadGrid.getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .forEach(btn -> addSmoothScaleHover((Button) btn));
        }
        amountField.setText("$0");
        cargarSaldo();
        sideNavController.setActiveButtonById("withdrawButton");
        UnaryOperator<TextFormatter.Change> amountFilter = change ->
        {
            String newText = change.getControlNewText();
            if (newText.matches("\\$?\\d*\\.?\\d*"))
                return change;
            return null;
        };
        amountField.setTextFormatter(new TextFormatter<>(amountFilter));
        amountField.setText("$0");
    }

    private void cargarSaldo()
    {
        try
        {
            String token = SessionManager.getInstance().getToken();
            Integer usuarioId = SessionManager.getInstance().getUsuarioId();
            if (token != null && !token.isEmpty() && usuarioId != null)
            {
                Long cuentaId = usuarioId.longValue();
                SaldoResponse saldo = consultarSaldo.consultarSaldo(cuentaId, token);
                saldoActual = saldo.getSaldo();
                saldoLabel.setText("$" + saldo.getSaldo() + " " + saldo.getMoneda());
            }
            else
                saldoLabel.setText("Saldo no disponible");
        }
        catch (Exception e)
        {
            saldoLabel.setText("Error al cargar saldo");
        }
    }

    @FXML private void pressDot()
    {
        String current = amountField.getText();
        String numeric = current.startsWith("$") ? current.substring(1) : current;
        if (numeric.contains("."))
            return;
        if (numeric.isEmpty() || numeric.equals("0"))
            numeric = "0.";
        else
            numeric = numeric + ".";
        amountField.setText("$" + numeric);
    }

    @FXML private void pressBackspace()
    {
        String current = amountField.getText();
        String numeric = current.startsWith("$") ? current.substring(1) : current;
        if (numeric.length() > 1)
            numeric = numeric.substring(0, numeric.length() - 1);
        else
            numeric = "0";
        amountField.setText("$" + numeric);
    }

    @FXML private void clearAll()
    {
        amountField.setText("$0");
    }

    private void addDigit(String digit)
    {
        String current = amountField.getText();
        String numeric = current.startsWith("$") ? current.substring(1) : current;
        if (numeric.equals("0") && !digit.equals("."))
            numeric = digit;
        else
            numeric = numeric + digit;
        amountField.setText("$" + numeric);
    }

    // ---- Montos rápidos (sin punto, pero se pueden agregar con decimales si se desea) ----
    @FXML private void setAmount100() { amountField.setText("$100"); }
    @FXML private void setAmount200() { amountField.setText("$200"); }
    @FXML private void setAmount500() { amountField.setText("$500"); }
    @FXML private void setAmount1000() { amountField.setText("$1000"); }

    // ---- Acciones ----
    @FXML
    private void handleCancel()
    {
        goToMainMenu();
    }

    @FXML
    private void handleConfirm()
    {
        String numeric = amountField.getText().replace("$", "").trim();
        BigDecimal monto;
        try
        {
            monto = new BigDecimal(numeric);
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

        if (saldoActual != null && monto.compareTo(saldoActual) > 0)
        {
            showAlert("Error", "Saldo insuficiente. Su saldo actual es: $" + saldoActual);
            return;
        }

        Long cuentaId = usuarioId.longValue();

        confirmButton.setDisable(true);
        cancelButton.setDisable(true);

        try
        {
            TransaccionResponse response = retiroService.retirar(
                    cuentaId,
                    new Monto(monto, "MXN"),
                    "CON_TARJETA",
                    "Retiro en cajero",
                    token
            );
            showAlert("Retiro exitoso", "ID transacción: " + response.getTransaccionId());
            goToMainMenu();
        }
        catch (HttpStatusCodeException e)
        {
            String mensaje = extraerMensajeError(e);
            if (e.getStatusCode().is4xxClientError())
                showAlert("Error en el retiro", mensaje);
            else
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
            String body = e.getResponseBodyAsString();
            if (body != null && !body.isEmpty())
            {
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