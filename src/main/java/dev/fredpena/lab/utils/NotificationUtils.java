package com.alphnology.carewave.utils;

import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class NotificationUtils {

    @Value("${application.notification.duration:3000}")
    private int duration;

    private void show(Notification notification, Icon icon, String msg) {
        notification.setDuration(duration);
        notification.setPosition(Notification.Position.TOP_END);

        final Div text = new Div(new Text(msg));

        final Button closeButton = new Button(new Icon("lumo", "cross"), event -> notification.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.getStyle().setMargin("0 0 0 var(--lumo-space-l)");

        final HorizontalLayout layout = new HorizontalLayout(icon, text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    public void error(com.vaadin.flow.component.Component component) {
        error(component.getTranslation(TranslationConstant.NOTIFICATION_ERROR));
    }

    public void error(String msg) {
        final Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);


        show(notification, VaadinIcon.CLOSE_CIRCLE.create(), msg);
    }

    public void error(ValidationException validationException) {
        error(null, validationException);
    }

    public void error(String msg, ValidationException validationException) {
        final Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        if (msg != null) {
            show(notification, VaadinIcon.CLOSE_CIRCLE.create(), msg);
        }

        validationException.getFieldValidationErrors().forEach(err -> err.getMessage().ifPresent(msg2 -> {
            String label = ((HasLabel) err.getBinding().getField()).getLabel();

            error(StringUtils.hasText(label) ? label + " -> " + msg2 : msg2);
        }));
    }

    public void success(com.vaadin.flow.component.Component component) {
        success(component.getTranslation(TranslationConstant.NOTIFICATION_SUCCESS));
    }

    public void success(String msg) {
        final Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        show(notification, VaadinIcon.CHECK_CIRCLE.create(), msg);
    }

    public void warning(String msg) {
        final Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        show(notification, VaadinIcon.WARNING.create(), msg);
    }

    public void primary(String msg) {
        final Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);

        show(notification, VaadinIcon.INFO_CIRCLE.create(), msg);
    }

    public void contrast(String msg) {
        final Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);

        show(notification, VaadinIcon.INFO_CIRCLE.create(), msg);
    }
}
