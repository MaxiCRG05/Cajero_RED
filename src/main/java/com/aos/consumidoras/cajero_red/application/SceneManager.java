package com.aos.consumidoras.cajero_red.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SceneManager
{
    private final ApplicationContext springContext;

    public SceneManager(ApplicationContext springContext)
    {
        this.springContext = springContext;
    }

    public void cambiarEscena(Stage stage, String fxmlPath)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Scene scene = stage.getScene();

            if (scene == null)
            {
                scene = new Scene(root);
                stage.setScene(scene);
            }
            else
            {
                scene.setRoot(root);
            }

            stage.setFullScreen(true);
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}