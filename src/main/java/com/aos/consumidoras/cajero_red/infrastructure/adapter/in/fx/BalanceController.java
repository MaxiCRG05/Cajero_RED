package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import com.aos.consumidoras.cajero_red.application.SceneManager;
import com.aos.consumidoras.cajero_red.application.SessionManager;
import com.aos.consumidoras.cajero_red.domain.model.dto.MovimientoDTO;
import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarMovimientosUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarSaldoUseCase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BalanceController extends BaseController
{
    @FXML private Text saldoText;
    @FXML private Text numeroCuentaText;
    @FXML private Text titularText;
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
        addSmoothScaleHover(backButton);
        cargarDatosUsuario();
        cargarSaldo();
        cargarTransacciones();
        if (sideNavController != null)
            sideNavController.setActiveButtonById("balanceButton");
    }

    private void cargarDatosUsuario()
    {
        try
        {
            String nombre = SessionManager.getInstance().getUsuarioNombre();
            String clabe = SessionManager.getInstance().getClabe();

            if (nombre != null && !nombre.isEmpty())
                titularText.setText(nombre.toUpperCase());
            else
                titularText.setText("TITULAR NO DISPONIBLE");

            if (clabe != null && !clabe.isEmpty())
            {
                String ultimos4 = clabe.length() >= 4 ? clabe.substring(clabe.length() - 4) : clabe;
                numeroCuentaText.setText("**** **** " + ultimos4);
            }
            else
            {
                numeroCuentaText.setText("**** **** 0000");
            }
        }
        catch (Exception e)
        {
            titularText.setText("ERROR");
            numeroCuentaText.setText("---");
            e.printStackTrace();
        }
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
                String saldoFormateado = String.format("$%,.2f %s", saldo.getSaldo(), saldo.getMoneda());
                saldoText.setText(saldoFormateado);
            }
            else
                saldoText.setText("Saldo no disponible");
        }
        catch (Exception e)
        {
            saldoText.setText("Error al cargar saldo");
            e.printStackTrace();
        }
    }

    private void cargarTransacciones()
    {
        try
        {
            String token = SessionManager.getInstance().getToken();
            Integer usuarioId = SessionManager.getInstance().getUsuarioId();
            if (token != null && !token.isEmpty() && usuarioId != null)
            {
                Long cuentaId = usuarioId.longValue();
                List<MovimientoDTO> movimientos = consultarMovimientos.consultarMovimientos(cuentaId, token);

                if (movimientos.size() > 5)
                    movimientos = movimientos.subList(0, 5);

                transactionsContainer.getChildren().clear();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                for (MovimientoDTO mov : movimientos)
                {
                    String tipo = mov.getTipoMovimiento();
                    String signo = "INGRESO".equalsIgnoreCase(tipo) ? "+" : "-";
                    String montoStr = signo + " $" + String.format("%,.2f", mov.getMonto()) + " " + mov.getMoneda();
                    String fechaStr = mov.getFecha().format(dateFormatter) + " " + mov.getFecha().format(timeFormatter);
                    String descripcion = mov.getDescripcion() != null ? mov.getDescripcion() : "Movimiento " + mov.getMovimientoId();
                    transactionsContainer.getChildren().add(createTransactionRow(descripcion, montoStr, fechaStr));
                }
            }
            else
                transactionsContainer.getChildren().add(createTransactionRow("No hay sesión activa", "", ""));
        }
        catch (Exception e)
        {
            transactionsContainer.getChildren().clear();
            transactionsContainer.getChildren().add(createTransactionRow("Error al cargar movimientos", e.getMessage(), ""));
            e.printStackTrace();
        }
    }

    private javafx.scene.Node createTransactionRow(String concepto, String monto, String fecha)
    {
        HBox row = new HBox();
        row.setStyle("-fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-alignment: CENTER_LEFT;");

        Text conceptText = new Text(concepto);
        conceptText.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Text montoText = new Text(monto);
        if (monto.startsWith("-"))
            montoText.setStyle("-fx-fill: #ba1a1a; -fx-font-weight: bold;");
        else if (monto.startsWith("+"))
            montoText.setStyle("-fx-fill: #00ae79; -fx-font-weight: bold;");
        else
            montoText.setStyle("-fx-fill: #5c5f61;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text fechaText = new Text(fecha);
        fechaText.setStyle("-fx-fill: #5c5f61; -fx-font-size: 12;");

        row.getChildren().addAll(conceptText, montoText, spacer, fechaText);
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