package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.MovimientoDTO;
import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarMovimientosUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarSaldoUseCase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BalanceController
{
    @FXML private Text saldoText;
    @FXML private VBox transactionsContainer;
    @FXML private Button backButton;
    @FXML private SideNavBarController sideNavController;

    @Autowired
    private ConsultarSaldoUseCase consultarSaldo;

    @Autowired
    private ConsultarMovimientosUseCase consultarMovimientos;

    @Autowired
    private SceneManager sceneManager;

    @FXML
    public void initialize()
    {
        cargarSaldo();
        cargarTransacciones();
        if (sideNavController != null)
            sideNavController.setActiveButtonById("balanceButton");
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
                saldoText.setText("$" + saldo.getSaldo() + " " + saldo.getMoneda());
            }
            else
                saldoText.setText("Saldo no disponible");
        }
        catch (Exception e)
        {
            saldoText.setText("Error al cargar saldo");
        }
    }

    private void cargarTransacciones() {
        try {
            String token = SessionManager.getInstance().getToken();
            Integer usuarioId = SessionManager.getInstance().getUsuarioId();
            if (token != null && !token.isEmpty() && usuarioId != null) {
                Long cuentaId = usuarioId.longValue();
                List<MovimientoDTO> movimientos = consultarMovimientos.consultarMovimientos(cuentaId, token);

                if (movimientos.size() > 5) {
                    movimientos = movimientos.subList(0, 5);
                }

                transactionsContainer.getChildren().clear();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                for (MovimientoDTO mov : movimientos) {
                    String tipo = mov.getTipoMovimiento();
                    String signo = "INGRESO".equalsIgnoreCase(tipo) ? "+" : "-";
                    String montoStr = signo + " $" + mov.getMonto().toPlainString() + " " + mov.getMoneda();
                    String fechaStr = mov.getFecha().format(dateFormatter) + " " + mov.getFecha().format(timeFormatter);
                    String descripcion = mov.getDescripcion() != null ? mov.getDescripcion() : "Movimiento " + mov.getMovimientoId();
                    transactionsContainer.getChildren().add(createTransactionRow(descripcion, montoStr, fechaStr));
                }
            } else {
                transactionsContainer.getChildren().add(createTransactionRow("No hay sesión activa", "", ""));
            }
        } catch (Exception e) {
            transactionsContainer.getChildren().clear();
            transactionsContainer.getChildren().add(createTransactionRow("Error al cargar movimientos", e.getMessage(), ""));
            e.printStackTrace();
        }
    }

    private javafx.scene.Node createTransactionRow(String concepto, String monto, String fecha)
    {
        VBox row = new VBox();
        row.setStyle("-fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        Text conceptText = new Text(concepto);
        conceptText.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        Text montoText = new Text(monto);
        if (monto.startsWith("-"))
            montoText.setStyle("-fx-fill: #ba1a1a; -fx-font-weight: bold;");
        else if (monto.startsWith("+"))
            montoText.setStyle("-fx-fill: #00ae79; -fx-font-weight: bold;");
        else
            montoText.setStyle("-fx-fill: #5c5f61;");
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
            Stage stage = (Stage) backButton.getScene().getWindow();
            sceneManager.cambiarEscena(stage, "/views/screens/Main.fxml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}