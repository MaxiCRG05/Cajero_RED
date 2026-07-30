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
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TransferController
{
    @FXML private TextField clabeField;
    @FXML private TextField beneficiarioField;
    @FXML private TextField amountField;
    @FXML private TextField conceptoField;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private SideNavBarController sideNavController;

    @Autowired
    private RealizarTransferenciaUseCase transferenciaService;

    @Autowired
    private SceneManager sceneManager;

    @FXML
    public void initialize()
    {
        amountField.setText("0");
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
        try
        {
            String clabe = clabeField.getText();
            if (clabe.length() != 18)
            {
                showAlert("Error", "La CLABE debe tener 18 dígitos.");
                return;
            }

            BigDecimal monto = new BigDecimal(amountField.getText());
            if (monto.compareTo(BigDecimal.ZERO) <= 0)
            {
                showAlert("Error", "El monto debe ser mayor a cero.");
                return;
            }

            String token = SessionManager.getInstance().getToken();
            Integer usuarioId = SessionManager.getInstance().getUsuarioId();
            if (usuarioId == null)
            {
                showAlert("Error", "Usuario no autenticado.");
                return;
            }
            Long cuentaOrigenId = usuarioId.longValue();

            TransferenciaResponse response = transferenciaService.transferir(
                    cuentaOrigenId,
                    clabe,
                    new Monto(monto, "MXN"),
                    conceptoField.getText(),
                    token
            );

            showAlert("Transferencia exitosa",
                    "ID transferencia: " + response.getTransferenciaId() +
                            "\nNuevo saldo origen: " + response.getSaldoOrigenNuevo().getCantidad() +
                            " " + response.getSaldoOrigenNuevo().getMoneda());
            goToMainMenu();
        }
        catch (Exception e)
        {
            showAlert("Error", e.getMessage());
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