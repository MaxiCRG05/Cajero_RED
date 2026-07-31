package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardInsertController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(CardInsertController.class);

    @FXML private Label cardIcon;
    @FXML private Text arrowIcon;
    @FXML private Button btnRetiroSinTarjeta;
    @FXML private Button btnSimularInsercion;

    @Autowired
    private AuthPort authPort;

    @Autowired
    private SceneManager sceneManager;

    @FXML
    public void initialize()
    {
        animaciones();
    }

    @FXML
    private void animaciones()
    {
        animacionMouse();
        animacionArrowIcon();
        animacionCardIcon();
        addSmoothScaleHover(btnSimularInsercion, btnRetiroSinTarjeta);
    }

    @FXML
    private void animacionMouse()
    {
        if (btnRetiroSinTarjeta != null)
        {
            btnRetiroSinTarjeta.setOnMouseEntered(event ->
            {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), btnRetiroSinTarjeta);
                st.setToX(1.05);
                st.setToY(1.05);
                st.setAutoReverse(false);
                st.play();

                btnRetiroSinTarjeta.setStyle("-fx-background-color: #0a8a5e; -fx-text-fill: white; -fx-background-radius: 30px; -fx-padding: 20px 48px; -fx-font-size: 22px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(11, 158, 110, 0.6), 20, 0, 0, 6); -fx-cursor: hand;");
            });

            btnRetiroSinTarjeta.setOnMouseExited(event ->
            {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), btnRetiroSinTarjeta);
                st.setToX(1.0);
                st.setToY(1.0);
                st.setAutoReverse(false);
                st.play();

                btnRetiroSinTarjeta.setStyle("-fx-background-color: #0b9e6e; -fx-text-fill: white; -fx-background-radius: 30px; -fx-padding: 20px 48px; -fx-font-size: 22px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(11, 158, 110, 0.4), 12, 0, 0, 4); -fx-cursor: hand;");
            });
        }
    }

    @FXML
    private void animacionArrowIcon()
    {
        if (arrowIcon != null)
        {
            TranslateTransition bounce = new TranslateTransition(Duration.millis(800), arrowIcon);
            bounce.setFromY(0);
            bounce.setToY(-12);
            bounce.setAutoReverse(true);
            bounce.setCycleCount(TranslateTransition.INDEFINITE);
            bounce.play();

            FillTransition glow = new FillTransition(Duration.millis(800), arrowIcon);
            Color originalColor = Color.valueOf("#0f1a2e");
            Color brightColor = Color.valueOf("#4edea3");
            glow.setFromValue(originalColor);
            glow.setToValue(brightColor);
            glow.setAutoReverse(true);
            glow.setCycleCount(FillTransition.INDEFINITE);
            glow.play();
        }

    }

    @FXML
    private void animacionCardIcon()
    {
        if (cardIcon != null)
        {
            RotateTransition toRight = new RotateTransition(Duration.millis(300), cardIcon);
            toRight.setFromAngle(-8);
            toRight.setToAngle(8);
            RotateTransition toLeft = new RotateTransition(Duration.millis(300), cardIcon);
            toLeft.setFromAngle(8);
            toLeft.setToAngle(-8);
            RotateTransition toCenter = new RotateTransition(Duration.millis(300), cardIcon);
            toCenter.setFromAngle(8);
            toCenter.setToAngle(0);
            PauseTransition pause = new PauseTransition(Duration.millis(200));
            SequentialTransition wobbleSequence = new SequentialTransition(toLeft, toRight, toCenter, pause);
            wobbleSequence.setCycleCount(SequentialTransition.INDEFINITE);
            wobbleSequence.play();
        }
    }

    @FXML
    public void handleInsertCard(ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) btnSimularInsercion.getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/AuthCard.fxml");
        }
        catch (Exception e)
        {
            logger.error("Error al abrir la pantalla de autenticación", e);
        }
    }

    @FXML
    public void handleWithdrawNoCard(ActionEvent event)
    {
        try
        {
            Stage stage = (Stage) btnSimularInsercion.getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/WithdrawNoCardCode.fxml");
        }
        catch (Exception e)
        {
            logger.error("Error al abrir la pantalla de Retiro sin Tarjeta", e);
        }
    }
}