package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarSaldoUseCase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @FXML private SideNavBarController sideNavController;

    @Autowired
    private ConsultarSaldoUseCase consultarSaldo;

    @Autowired
    private SceneManager sceneManager;

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
        sideNavController.setActiveButtonById("homeButton");
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
        navegarA("/views/screens/Withdraw.fxml");
    }

    @FXML
    public void goToDeposit()
    {
        navegarA("/views/screens/Deposit.fxml");
    }

    @FXML
    public void goToBalance()
    {
        navegarA("/views/screens/Balance.fxml");
    }

    @FXML
    public void goToTransfer()
    {
        navegarA("/views/screens/Transfer.fxml");
    }

    @FXML
    public void goToWithdrawNoCard()
    {
        logger.info("Navegando a Retiro sin Tarjeta...");
    }

    private void navegarA(String fxmlPath)
    {
        try
        {
            Stage stage = (Stage) withdrawCard.getScene().getWindow();
            sceneManager.cambiarEscena(stage, fxmlPath);
        }
        catch (Exception e)
        {
            logger.error("Error detallado al cargar la vista FXML: {}", fxmlPath, e);
        }
    }
}