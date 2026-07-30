package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SideNavBarController
{
    private final ApplicationContext applicationContext;
    private final SceneManager sceneManager;

    @FXML private Text userNameLabel;
    @FXML private Button homeButton;
    @FXML private Button withdrawButton;
    @FXML private Button depositButton;
    @FXML private Button balanceButton;
    @FXML private Button transferButton;
    @FXML private Button logoutButton;
    @FXML private Button emergencyButton;

    public SideNavBarController(ApplicationContext applicationContext, SceneManager sceneManager)
    {
        this.applicationContext = applicationContext;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize()
    {
        String nombre = SessionManager.getInstance().getUsuarioNombre();
        if (nombre != null)
            userNameLabel.setText(nombre);
    }

    @FXML private void handleHome(ActionEvent event)
    {
        setActiveButton(homeButton);
        navegarA("/views/screens/Main.fxml", event);
    }

    @FXML private void handleWithdraw(ActionEvent event)
    {
        setActiveButton(withdrawButton);
        navegarA("/views/screens/Withdraw.fxml", event);
    }

    @FXML private void handleDeposit(ActionEvent event)
    {
        setActiveButton(depositButton);
        navegarA("/views/screens/Deposit.fxml", event);
    }

    @FXML private void handleBalance(ActionEvent event)
    {
        setActiveButton(balanceButton);
        navegarA("/views/screens/Balance.fxml", event);
    }

    @FXML private void handleTransfer(ActionEvent event)
    {
        setActiveButton(transferButton);
        navegarA("/views/screens/Transfer.fxml", event);
    }

    @FXML private void handleLogout(ActionEvent event)
    {
        SessionManager.getInstance().setToken(null);
        SessionManager.getInstance().setUsuarioId(null);
        SessionManager.getInstance().setUsuarioNombre(null);
        navegarA("/views/screens/CardInsert.fxml", event);
    }

    @FXML private void handleEmergency(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Asistencia");
        alert.setHeaderText("Ayuda de Emergencia");
        alert.setContentText("Para recibir asistencia o reportar un problema, comuníquese con el soporte técnico o utilice el teléfono de emergencia integrado.");
        alert.showAndWait();
    }

    private void setActiveButton(Button selectedButton)
    {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #5c5f61; -fx-background-radius: 12; -fx-padding: 12;";
        String activeStyle = "-fx-background-color: #0b315e; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 12; -fx-font-weight: bold;";

        homeButton.setStyle(defaultStyle);
        withdrawButton.setStyle(defaultStyle);
        depositButton.setStyle(defaultStyle);
        balanceButton.setStyle(defaultStyle);
        transferButton.setStyle(defaultStyle);

        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ba1a1a; -fx-background-radius: 12; -fx-padding: 12; -fx-border-color: #ba1a1a; -fx-border-radius: 12;");

        selectedButton.setStyle(activeStyle);
    }

    public void setActiveButtonById(String buttonId)
    {
        Button target = null;
        switch (buttonId)
        {
            case "homeButton": target = homeButton; break;
            case "withdrawButton": target = withdrawButton; break;
            case "depositButton": target = depositButton; break;
            case "balanceButton": target = balanceButton; break;
            case "transferButton": target = transferButton; break;
        }
        if (target != null)
        {
            setActiveButton(target);
        }
    }

    private void navegarA(String fxmlPath, ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            sceneManager.cambiarEscena(stage, fxmlPath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setUserName(String name)
    {
        if (userNameLabel != null)
        {
            userNameLabel.setText(name);
        }
    }
}