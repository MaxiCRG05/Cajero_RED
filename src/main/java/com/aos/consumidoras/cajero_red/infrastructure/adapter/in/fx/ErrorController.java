package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ErrorController
{
    @FXML private Text errorMessage;
    @FXML private Button retryButton;
    @FXML private Button cancelButton;

    private ApplicationContext springContext;

    public void setSpringContext(ApplicationContext context)
    {
        this.springContext = context;
    }

    public void setErrorMessage(String message)
    {
        errorMessage.setText(message);
    }

    @FXML
    private void handleRetry()
    {
    }

    @FXML
    private void handleCancel()
    {
        goToMainMenu();
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
}