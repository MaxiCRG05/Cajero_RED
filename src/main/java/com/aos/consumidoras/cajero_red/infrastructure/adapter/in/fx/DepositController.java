package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransaccionResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.RealizarDepositoUseCase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DepositController
{
    @FXML private TextField clabeField;
    @FXML private TextField amountField;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;

    @Autowired
    private RealizarDepositoUseCase depositoService;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void initialize()
    {
        amountField.setText("0");
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

    @FXML private void pressBackspace()
    {
        String current = amountField.getText();
        if (current.length() > 1)
        {
            amountField.setText(current.substring(0, current.length() - 1));
        }
        else
        {
            amountField.setText("0");
        }
    }

    @FXML private void clearAll()
    {
        amountField.setText("0");
    }

    private void addDigit(String digit)
    {
        String current = amountField.getText();
        if (current.equals("0"))
        {
            amountField.setText(digit);
        }
        else
        {
            amountField.setText(current + digit);
        }
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

            TransaccionResponse response = depositoService.depositar(
                    clabe,
                    new Monto(monto, "MXN"),
                    "REF-" + System.currentTimeMillis(),
                    "Depósito en cajero",
                    token
            );

            showAlert("Depósito exitoso", "ID transacción: " + response.getTransaccionId());
            goToMainMenu();
        }
        catch (Exception e)
        {
            showAlert("Error", e.getMessage());
        }
    }

    private void goToMainMenu()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/screens/Main.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/views/styles.css").toExternalForm());
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cajero RED - Menú");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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