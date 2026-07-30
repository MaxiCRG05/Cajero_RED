package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ErrorController
{
    @FXML private Text errorMessage;
    @FXML private Button retryButton;
    @FXML private Button cancelButton;
    @FXML private SideNavBarController sideNavController;

    private ApplicationContext springContext;

    @Autowired
    private SceneManager sceneManager;

    public void setSpringContext(ApplicationContext context)
    {
        this.springContext = context;
    }

    public void setErrorMessage(String message)
    {
        errorMessage.setText(message);
    }

    @FXML
    private void handleRetry() { }

    @FXML
    private void handleCancel()
    {
        goToMainMenu();
    }

    private void goToMainMenu()
    {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        sceneManager.cambiarEscena(stage, "/views/screens/Main.fxml");
    }
}