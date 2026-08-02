package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.MovimientoDTO;
import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarMovimientosUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarSaldoUseCase;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MainController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML private Text welcomeMessage;
    @FXML private Label saldoLabel;
    @FXML private VBox transactionsContainer;
    @FXML private Button withdrawCard;
    @FXML private Button depositCard;
    @FXML private Button balanceCard;
    @FXML private Button transferCard;
    @FXML private Button btnVerSaldo;
    @FXML private SideNavBarController sideNavController;

    @Autowired private ConsultarSaldoUseCase consultarSaldo;
    @Autowired private ConsultarMovimientosUseCase consultarMovimientos;
    @Autowired private SceneManager sceneManager;

    @FXML
    public void initialize()
    {
        addSmoothScaleHover(withdrawCard, depositCard, balanceCard, transferCard, btnVerSaldo);
        cargarSaludo();
        cargarSaldoYMovimientos();
        sideNavController.setActiveButtonById("homeButton");
    }

    private void cargarSaludo()
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
        welcomeMessage.setText(saludo + ", " + (nombre != null ? nombre : "Usuario"));
    }

    private void cargarSaldoYMovimientos()
    {
        String token = SessionManager.getInstance().getToken();
        Integer usuarioId = SessionManager.getInstance().getUsuarioId();
        if (token == null || usuarioId == null)
        {
            saldoLabel.setText("Saldo no disponible");
            return;
        }
        Long cuentaId = usuarioId.longValue();

        Task<Void> task = new Task<>()
        {
            private SaldoResponse saldo;
            private List<MovimientoDTO> movimientos;

            @Override
            protected Void call() throws Exception
            {
                saldo = consultarSaldo.consultarSaldo(cuentaId, token);
                movimientos = consultarMovimientos.consultarMovimientos(cuentaId, token);
                if (movimientos.size() > 5)
                    movimientos = movimientos.subList(0, 5);
                return null;
            }

            @Override
            protected void succeeded()
            {
                Platform.runLater(() ->
                {
                    if (saldo != null)
                        saldoLabel.setText("$" + saldo.getSaldo() + " " + saldo.getMoneda());
                    else
                        saldoLabel.setText("Error al cargar saldo");

                    if (movimientos != null && !movimientos.isEmpty())
                    {
                        transactionsContainer.getChildren().clear();
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                        for (MovimientoDTO mov : movimientos)
                        {
                            String tipo = mov.getTipoMovimiento();
                            String signo = "INGRESO".equalsIgnoreCase(tipo) ? "+" : "-";
                            String montoStr = signo + " $" + String.format("%,.2f", mov.getMonto()) + " " + mov.getMoneda();
                            String fechaStr = mov.getFecha().format(dateFormatter) + " " + mov.getFecha().format(timeFormatter);
                            transactionsContainer.getChildren().add(createTransactionRow(tipo, montoStr, fechaStr));
                        }
                    }
                    else
                    {
                        transactionsContainer.getChildren().clear();
                        transactionsContainer.getChildren().add(createTransactionRow("", "No hay movimientos recientes", ""));
                    }
                });
            }

            @Override
            protected void failed()
            {
                Platform.runLater(() ->
                {
                    saldoLabel.setText("Error al cargar saldo");
                    transactionsContainer.getChildren().clear();
                    transactionsContainer.getChildren().add(createTransactionRow("", "Error al cargar movimientos", ""));
                });
            }
        };
        new Thread(task).start();
    }

    private javafx.scene.Node createTransactionRow(String tipo, String monto, String fecha)
    {
        HBox row = new HBox();
        row.setStyle("-fx-padding: 10 0; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-alignment: CENTER_LEFT;");

        Text tipoText = new Text(tipo != null ? tipo : "");
        tipoText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #2563eb;");
        tipoText.setWrappingWidth(180);

        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);

        Text montoText = new Text(monto);
        if (monto.startsWith("-"))
            montoText.setStyle("-fx-fill: #ba1a1a; -fx-font-weight: bold; -fx-font-size: 15;");
        else if (monto.startsWith("+"))
            montoText.setStyle("-fx-fill: #00ae79; -fx-font-weight: bold; -fx-font-size: 15;");
        else
            montoText.setStyle("-fx-fill: #5c5f61; -fx-font-size: 15;");

        Region spacerRight = new Region();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        Text fechaText = new Text(fecha);
        fechaText.setStyle("-fx-fill: #5c6f87; -fx-font-size: 12;");

        row.getChildren().addAll(tipoText, spacerLeft, montoText, spacerRight, fechaText);
        return row;
    }

    @FXML public void goToWithdraw() { navegarA("/views/screens/Withdraw.fxml"); }
    @FXML public void goToDeposit() { navegarA("/views/screens/Deposit.fxml"); }
    @FXML public void goToBalance() { navegarA("/views/screens/Balance.fxml"); }
    @FXML public void goToTransfer() { navegarA("/views/screens/Transfer.fxml"); }

    private void navegarA(String fxmlPath)
    {
        try
        {
            Stage stage = (Stage) withdrawCard.getScene().getWindow();
            sceneManager.cambiarEscena(stage, fxmlPath);
        }
        catch (Exception e)
        {
            logger.error("Error al navegar a {}", fxmlPath, e);
        }
    }
}