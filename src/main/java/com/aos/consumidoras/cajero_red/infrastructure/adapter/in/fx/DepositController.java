package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransaccionResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.RealizarDepositoUseCase;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositController {
    @FXML private TextField amountField;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private SideNavBarController sideNavController;

    @Autowired private RealizarDepositoUseCase depositoService;
    @Autowired private SceneManager sceneManager;

    @FXML
    public void initialize() {
        amountField.setText("0");
        sideNavController.setActiveButtonById("depositButton");
    }

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

    @FXML private void pressBackspace() {
        String current = amountField.getText();
        if (current.length() > 1) {
            amountField.setText(current.substring(0, current.length() - 1));
        } else {
            amountField.setText("0");
        }
    }

    @FXML private void clearAll() {
        amountField.setText("0");
    }

    private void addDigit(String digit) {
        String current = amountField.getText();
        if (current.equals("0")) {
            amountField.setText(digit);
        } else {
            amountField.setText(current + digit);
        }
    }

    @FXML
    private void handleCancel() {
        goToMainMenu();
    }

    @FXML
    private void handleConfirm() {
        try {
            BigDecimal monto = new BigDecimal(amountField.getText());
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Error", "El monto debe ser mayor a cero.");
                return;
            }

            String clabe = SessionManager.getInstance().getClabe();
            if (clabe == null || clabe.isEmpty()) {
                showAlert("Error", "No se pudo obtener la CLABE de la cuenta. Intente de nuevo.");
                return;
            }

            confirmButton.setDisable(true);
            cancelButton.setDisable(true);

            Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
            progressAlert.setTitle("Depósito en proceso");
            progressAlert.setHeaderText("Contando billetes...");
            progressAlert.setContentText("Por favor, espere mientras se cuentan los billetes.");
            progressAlert.setGraphic(null);
            progressAlert.show();

            Task<TransaccionResponse> task = new Task<>() {
                @Override
                protected TransaccionResponse call() throws Exception {
                    Thread.sleep(2000);
                    String token = SessionManager.getInstance().getToken();
                    return depositoService.depositar(
                            clabe,
                            new Monto(monto, "MXN"),
                            "REF-" + System.currentTimeMillis(),
                            "Depósito en cajero",
                            token
                    );
                }
            };

            task.setOnSucceeded(event -> {
                progressAlert.close();
                TransaccionResponse response = task.getValue();
                showAlert("Depósito exitoso", "ID transacción: " + response.getTransaccionId());
                confirmButton.setDisable(false);
                cancelButton.setDisable(false);
                goToMainMenu();
            });

            task.setOnFailed(event -> {
                progressAlert.close();
                Throwable ex = task.getException();
                showAlert("Error", "Error al realizar el depósito: " + ex.getMessage());
                confirmButton.setDisable(false);
                cancelButton.setDisable(false);
            });

            new Thread(task).start();

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            confirmButton.setDisable(false);
            cancelButton.setDisable(false);
        }
    }

    private void goToMainMenu() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        sceneManager.cambiarEscena(stage, "/views/screens/Main.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}