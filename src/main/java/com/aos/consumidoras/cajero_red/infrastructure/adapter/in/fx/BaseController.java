package com.aos.consumidoras.cajero_red.infrastructure.adapter.in.fx;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.util.Collection;

public abstract class BaseController
{
    protected void addSmoothScaleHover(Button... buttons)
    {
        for (Button btn : buttons) {
            if (btn == null) continue;
            btn.setOnMouseEntered(e ->
            {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
                st.setToX(1.05);
                st.setToY(1.05);
                st.play();
            });
            btn.setOnMouseExited(e ->
            {
                ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            });
        }
    }

    protected void addSmoothScaleHover(Collection<Button> buttons)
    {
        addSmoothScaleHover(buttons.toArray(new Button[0]));
    }
}