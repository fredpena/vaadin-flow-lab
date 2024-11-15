package dev.fredpena.lab.utils;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import dev.fredpena.lab.data.Person;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * @author me@fredpena.dev
 * @created 15/11/2024  - 13:22
 */
public final class CommonUtils {

    private CommonUtils() {
    }


    public static ConfirmDialog saveOrUpdateDialog(Runnable runnable) {
        var confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Unsaved changes");
        confirmDialog.setText("There are unsaved changes. Do you want to discard them or continue?");

        confirmDialog.setRejectable(true);
        confirmDialog.setRejectText("Discard");

        confirmDialog.setConfirmText("Continue");
        confirmDialog.addConfirmListener(event -> {

            runnable.run();

            NotificationUtils.success("The transaction was successful.");
        });
        return confirmDialog;
    }

    public static ConfirmDialog deleteDialog(Runnable runnable) {
        var confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Deleting record");
        confirmDialog.setText("You are deleting a record. Do you want to discard it or continue?");

        confirmDialog.setRejectable(true);
        confirmDialog.setRejectText("Discard");
        confirmDialog.setRejectButtonTheme("tertiary");

        confirmDialog.setConfirmText("Continue");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.addConfirmListener(event -> {

            runnable.run();

            NotificationUtils.success("The record was deleted.");
        });
        return confirmDialog;
    }


    public static Predicate predicateLike(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder builder, String field, String filter) {

        return builder.like(builder.lower(root.get(field)), "%" + filter + "%");
    }
}
