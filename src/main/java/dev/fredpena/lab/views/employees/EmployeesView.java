package dev.fredpena.lab.views.employees;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;

import static dev.fredpena.lab.utils.CSSUtility.*;

/**
 * @author me@fredpena.dev
 * @created 15/11/2024  - 17:06
 */

@Slf4j
@PageTitle("Employees")
@Route("employees")
@Menu(order = 4, icon = "line-awesome/svg/user-solid.svg")
@Uses(Icon.class)
public class EmployeesView extends Main {

    // This view was inspired by the following repository
    // https://github.com/vesanieminen/figma-design-to-flow-app
    public EmployeesView() {
        addClassNames(
                LumoUtility.Display.GRID,
                LumoUtility.Margin.Horizontal.AUTO,
                LumoUtility.Padding.Horizontal.MEDIUM,
                COLUMN_GAP,
                CONTENT_MAX_WIDTH,
                MARGIN_TOP_XXL
        );

        add(createHeadingAndButtonSection(), createFormSection(), createProfilePictureAndTaskSection());
    }

    private static Section createHeadingAndButtonSection() {
        final var newEmployeeH1 = new H1("New Employee");
        final var buttonDiv = createButtonDiv();
        final var headingDiv = new Section(newEmployeeH1, buttonDiv);
        headingDiv.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.Gap.XLARGE,
                TWO_COLUMNS_WHEN_OVER_800PX,
                LumoUtility.JustifyContent.BETWEEN
        );
        return headingDiv;
    }

    private static Div createButtonDiv() {
        final var saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        final var cancelButton = new Button("Cancel");
        final var buttonDiv = new Div(saveButton, cancelButton);
        buttonDiv.addClassNames(
                LumoUtility.Gap.MEDIUM,
                FLEX_OVER_600PX
        );
        return buttonDiv;
    }

    private static Section createFormSection() {
        final var personalDetailsH3 = new H3("Personal details");
        final var firstnameTextField = new TextField("First name");
        final var lastnameTextField = new TextField("Last name");
        final var emailField = new EmailField("Email");

        final var personalDetailsForm = createForm(
                firstnameTextField,
                lastnameTextField,
                emailField
        );
        personalDetailsForm.setColspan(emailField, 2);

        final var jobDetailsH3 = new H3("Job details");
        jobDetailsH3.addClassNames(
                MARGIN_TOP_XXL
        );
        final var startDate = new DatePicker("Start date");
        startDate.setPlaceholder("Pick a date");
        final var needsOnboardingCheckbox = new Checkbox("Needs onboarding");
        needsOnboardingCheckbox.setValue(true);
        final var jobTitleField = new TextField("Job title");
        final var teamComboBox = new ComboBox<>("Team");
        final var supervisorComboBox = new ComboBox<>("Supervisor");
        supervisorComboBox.setInvalid(true);
        supervisorComboBox.setErrorMessage("Please select a person");
        final var jobDetailsForm = createForm(
                startDate,
                needsOnboardingCheckbox,
                jobTitleField,
                teamComboBox,
                supervisorComboBox
        );
        jobDetailsForm.setColspan(jobTitleField, 2);
        jobDetailsForm.setColspan(teamComboBox, 2);
        jobDetailsForm.setColspan(supervisorComboBox, 2);

        final var section = new Section(
                personalDetailsH3,
                personalDetailsForm,
                jobDetailsH3,
                jobDetailsForm
        );

        section.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                MARGIN_TOP_XXL,
                COLUMN_MAX_WIDTH_WHEN_OVER_800PX
        );
        return section;
    }

    private static FormLayout createForm(AbstractField... fields) {
        final var formLayout = new FormLayout(fields);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20rem", 2)
        );
        return formLayout;
    }

    private static Section createProfilePictureAndTaskSection() {
        final var profilePictureSpan = new Span("Profile picture");
        final var upload = new Upload();
        upload.setHeight("260px");
        final var tasksForNewEmployeeH3 = new H3("Tasks for new employee");
        tasksForNewEmployeeH3.addClassNames(
                LumoUtility.Margin.Top.LARGE,
                LumoUtility.Margin.Bottom.SMALL
        );
        final var taskListBox = new MultiSelectListBox<Task>();
        taskListBox.setItemLabelGenerator(item -> item.caption);
        taskListBox.setItems(Task.values());
        taskListBox.select(Task.HEALTH_CHECK, Task.MEETING_WITH_CEO);
        final var cardDiv = new Div(
                profilePictureSpan,
                upload,
                tasksForNewEmployeeH3,
                taskListBox
        );
        cardDiv.addClassNames(
                CARD_BACKGROUND_COLOR,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE
        );

        final var section = new Section(cardDiv);
        section.addClassNames(
                MARGIN_TOP_XXL,
                COLUMN_MAX_WIDTH_WHEN_OVER_800PX
        );
        return section;
    }

    enum Task {
        HEALTH_CHECK("Health check"),
        MEETING_WITH_HR("Meeting with HR"),
        MEETING_WITH_CEO("Meeting with CEO"),
        ITEM("Item");

        public final String caption;

        Task(String caption) {
            this.caption = caption;
        }

    }
}
