package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarSaldoUseCase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
public class MainController
{
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML private Text welcomeMessage;
    @FXML private Label saldoLabel;
    @FXML private Button withdrawCard;
    @FXML private Button depositCard;
    @FXML private Button balanceCard;
    @FXML private Button transferCard;

    @FXML private Button withdrawButton;
    @FXML private Button depositButton;
    @FXML private Button balanceButton;
    @FXML private Button transferButton;

    @FXML private Button withdrawNoCardButton;

    @Autowired
    private ConsultarSaldoUseCase consultarSaldo;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void initialize()
    {
        String nombre = SessionManager.getInstance().getUsuarioNombre();
        int hour = LocalTime.now().getHour();
        String saludo;
        if (hour < 12)
            saludo = "Buenos Días";
        else if (hour < 20)
            saludo = "Buenas Tardes";
        else
            saludo = "Buenas Noches";

        welcomeMessage.setText(saludo + ", " + nombre);

        if (withdrawButton != null) withdrawButton.setOnAction(e -> goToWithdraw());
        if (depositButton != null) depositButton.setOnAction(e -> goToDeposit());
        if (balanceButton != null) balanceButton.setOnAction(e -> goToBalance());
        if (transferButton != null) transferButton.setOnAction(e -> goToTransfer());

        withdrawCard.setOnAction(e -> goToWithdraw());
        depositCard.setOnAction(e -> goToDeposit());
        balanceCard.setOnAction(e -> goToBalance());
        transferCard.setOnAction(e -> goToTransfer());

        cargarSaldo();
    }

    private void cargarSaldo()
    {
        try
        {
            String token = SessionManager.getInstance().getToken();
            if (token != null && !token.isEmpty())
            {
                Long cuentaId = 1L;
                SaldoResponse saldo = consultarSaldo.consultarSaldo(cuentaId, token);
                if (saldoLabel != null)
                    saldoLabel.setText("Saldo: $" + saldo.getSaldo() + " " + saldo.getMoneda());
            }
            else
            {
                if (saldoLabel != null)
                    saldoLabel.setText("Saldo: No disponible");
            }
        }
        catch (Exception e)
        {
            if (saldoLabel != null)
                saldoLabel.setText("Error al cargar saldo: " + e.getMessage());
            logger.error("Error al obtener el saldo del usuario", e);
        }
    }

    @FXML
    public void goToWithdraw()
    {
        navigateTo("/views/screens/Withdraw.fxml", "Retiro de Efectivo");
    }

    @FXML
    public void goToDeposit()
    {
        navigateTo("/views/screens/Deposit.fxml", "Depósito");
    }

    @FXML
    public void goToBalance()
    {
        navigateTo("/views/screens/Balance.fxml", "Consulta de Saldo");
    }

    @FXML
    public void goToTransfer()
    {
        navigateTo("/views/screens/Transfer.fxml", "Transferencia SPEI");
    }

    @FXML
    public void goToWithdrawNoCard()
    {
        logger.info("Navegando a Retiro sin Tarjeta...");
    }

    private void navigateTo(String fxmlPath, String title)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/views/styles.css").toExternalForm());
            Stage stage = (Stage) withdrawCard.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cajero RED - " + title);
        }
        catch (Exception e)
        {
            logger.error("Error detallado al cargar la vista FXML: {}", fxmlPath, e);
        }
    }
}