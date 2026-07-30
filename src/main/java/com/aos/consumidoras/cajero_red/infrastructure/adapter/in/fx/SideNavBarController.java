package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SideNavBarController
{

    private final ApplicationContext applicationContext;

    @FXML
    private Text userNameLabel;

    @FXML
    private Button withdrawButton;

    @FXML
    private Button depositButton;

    @FXML
    private Button balanceButton;

    @FXML
    private Button emergencyButton;

    private Pane contentArea;

    public SideNavBarController(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize()
    {
    }

    @FXML
    private void handleWithdraw(ActionEvent event)
    {
        setActiveButton(withdrawButton);
        loadView("/views/screens/Withdraw.fxml");
    }

    @FXML
    private void handleDeposit(ActionEvent event)
    {
        setActiveButton(depositButton);
        loadView("/views/screens/Deposit.fxml");
    }

    @FXML
    private void handleBalance(ActionEvent event)
    {
        setActiveButton(balanceButton);
        loadView("/views/screens/Balance.fxml");
    }

    @FXML
    private void handleEmergency(ActionEvent event)
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

        withdrawButton.setStyle(defaultStyle);
        depositButton.setStyle(defaultStyle);
        balanceButton.setStyle(defaultStyle);

        selectedButton.setStyle(activeStyle);
    }

    private void loadView(String fxmlPath)
    {
        try
        {
            if (contentArea != null)
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                loader.setControllerFactory(applicationContext::getBean);
                Parent view = loader.load();
                contentArea.getChildren().setAll(view);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setContentArea(Pane contentArea)
    {
        this.contentArea = contentArea;
    }

    public void setUserName(String name)
    {
        if (userNameLabel != null)
        {
            userNameLabel.setText(name);
        }
    }
}