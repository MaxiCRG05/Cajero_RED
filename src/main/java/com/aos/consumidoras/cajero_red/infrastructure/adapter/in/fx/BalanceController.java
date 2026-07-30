package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarSaldoUseCase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BalanceController
{
    @FXML private Text saldoText;
    @FXML private VBox transactionsContainer;
    @FXML private Button backButton;

    @Autowired
    private ConsultarSaldoUseCase consultarSaldo;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void initialize()
    {
        cargarSaldo();
        cargarTransacciones();
    }

    private void cargarSaldo()
    {
        try
        {
            String token = SessionManager.getInstance().getToken();
            Long cuentaId = 1L;
            SaldoResponse saldo = consultarSaldo.consultarSaldo(cuentaId, token);
            saldoText.setText("$" + saldo.getSaldo() + " " + saldo.getMoneda());
        }
        catch (Exception e)
        {
            saldoText.setText("Error al cargar saldo");
        }
    }

    private void cargarTransacciones()
    {
        transactionsContainer.getChildren().add(createTransactionRow("Compra Supermercado", "- $142.50", "24 May 2024"));
        transactionsContainer.getChildren().add(createTransactionRow("Retiro ATM", "- $200.00", "23 May 2024"));
        transactionsContainer.getChildren().add(createTransactionRow("Depósito Nómina", "+ $2,100.00", "20 May 2024"));
    }

    private javafx.scene.Node createTransactionRow(String concepto, String monto, String fecha)
    {
        VBox row = new VBox();
        row.setStyle("-fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        Text conceptText = new Text(concepto);
        conceptText.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        Text montoText = new Text(monto);
        if (monto.startsWith("-"))
        {
            montoText.setStyle("-fx-fill: #ba1a1a; -fx-font-weight: bold;");
        }
        else
        {
            montoText.setStyle("-fx-fill: #00ae79; -fx-font-weight: bold;");
        }
        Text fechaText = new Text(fecha);
        fechaText.setStyle("-fx-fill: #5c5f61; -fx-font-size: 12;");
        row.getChildren().addAll(conceptText, montoText, fechaText);
        return row;
    }

    @FXML
    private void goBackToMain()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/screens/Main.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/views/styles.css").toExternalForm());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cajero RED - Menú");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}