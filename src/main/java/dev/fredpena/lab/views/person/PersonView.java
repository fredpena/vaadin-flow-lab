package dev.fredpena.lab.views.person;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.HasClearButton;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import dev.fredpena.lab.data.Person;
import dev.fredpena.lab.services.PersonService;
import dev.fredpena.lab.utils.CommonUtils;
import dev.fredpena.lab.utils.NotificationUtils;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.fredpena.lab.utils.CommonUtils.predicateLike;

/**
 * @author me@fredpena.dev
 * @created 14/11/2024  - 19:16
 */
@Slf4j
@PageTitle("Person")
@Route("person")
@Menu(order = 2, icon = "line-awesome/svg/user-solid.svg")
@Uses(Icon.class)
public class PersonView extends VerticalLayout {

    private final TextField firstName = new TextField("First Name", "Enter first name");
    private final TextField lastName = new TextField("Last Name", "Enter last name");
    private final TextField email = new TextField("Email", "Enter email address");
    private final DatePicker dateOfBirth = new DatePicker("Date Of Birth");
    private final TextField address = new TextField("Address", "Enter address");
    private final TextField city = new TextField("City", "Enter city");
    private final TextField state = new TextField("State", "Enter state");
    private final TextField zipCode = new TextField("Zip Code", "Enter zip code");
    private final TextField country = new TextField("Country", "Enter country");
    private final TextField phone = new TextField("Phone", "Enter phone number");
    private final Checkbox newsletter = new Checkbox("Has newsletter");
    private final TextField username = new TextField("Username", "Enter username");
    private final PasswordField password = new PasswordField("Password", "Enter password");
    private final PasswordField repeatPassword = new PasswordField("Confirm Password", "Re-enter password");
    private final MultiSelectComboBox<String> segments = new MultiSelectComboBox<>("Segments");

    private final Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK_CIRCLE));
    private final Button newButton = new Button("New", new Icon(VaadinIcon.PLUS_CIRCLE));
    private final Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));


    private final Grid<Person> grid = new Grid<>(Person.class, false);
    private final TextField searchField = new TextField();
    private final Button toggleGridButton = new Button("Toggle Grid", new Icon(VaadinIcon.EYE));

    private final Binder<Person> binder = new BeanValidationBinder<>(Person.class);
    private final PersonService personService;
    private Person element;

    PersonView(PersonService personService) {
        this.personService = personService;
        grid.setVisible(false);
        searchField.setVisible(false);
        deleteButton.setEnabled(false);
        toggleGridButton.addClickListener(e -> {
            grid.setVisible(!grid.isVisible());
            searchField.setVisible(!searchField.isVisible());
            toggleGridButton.setIcon(new Icon(grid.isVisible() ? VaadinIcon.EYE_SLASH : VaadinIcon.EYE));
            searchField.focus();
        });

        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.getStyle().setPadding("0").setMargin("0");
        searchField.addValueChangeListener(e -> grid.getDataProvider().refreshAll());

        binder.bindInstanceFields(this);
        binder.getFields().forEach(field -> {
            if (field instanceof HasClearButton clear) {
                clear.setClearButtonVisible(true);
            }
        });

        Specification<Person> filter = (root, query, builder) -> {
            Order order = builder.asc(root.get("firstName"));
            assert query != null;
            query.orderBy(order);

            final String value = searchField.getValue().toLowerCase();

            Predicate predicateFirstName = predicateLike(root, query, builder, "firstName", value);
            Predicate predicateLastName = predicateLike(root, query, builder, "lastName", value);
            Predicate predicateAddress = predicateLike(root, query, builder, "address", value);
            Predicate predicateCity = predicateLike(root, query, builder, "city", value);
            Predicate predicateState = predicateLike(root, query, builder, "state", value);
            Predicate predicateZipCode = predicateLike(root, query, builder, "zipCode", value);
            Predicate predicateCountry = predicateLike(root, query, builder, "country", value);

            List<Predicate> andPredicates = new ArrayList<>(List.of(predicateFirstName, predicateLastName, predicateAddress,
                    predicateCity, predicateState, predicateZipCode, predicateCountry));

            return builder.or(andPredicates.toArray(Predicate[]::new));
        };

        grid.setItems(query ->
                personService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)), filter)
                        .stream());

        grid.addColumn(Person::getFirstName).setAutoWidth(true).setHeader("First Name");
        grid.addColumn(Person::getLastName).setAutoWidth(true).setHeader("Last Name");
        grid.addColumn(Person::getEmail).setAutoWidth(true).setHeader("Email");
        grid.addColumn(Person::getDateOfBirth).setAutoWidth(true).setHeader("Date Of Birth");
        grid.addColumn(Person::getAddress).setAutoWidth(true).setHeader("Address");
        grid.addColumn(Person::getCountry).setAutoWidth(true).setHeader("Country");
        grid.addColumn(Person::getPhone).setAutoWidth(true).setHeader("Phone");

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(person -> {
                    binder.setBean(person);
                    element = person;
                    deleteButton.setEnabled(true);
                }));

        saveButton.addClickListener(this::saveOrUpdate);
        deleteButton.addClickListener(this::delete);
        newButton.addClickListener(click -> {
            deleteButton.setEnabled(false);
            element = null;
            binder.setBean(null);
        });

        password.setHelperText("The password must be at least 8 characters long, include one capital letter, one number, and one special character.");
        repeatPassword.setHelperText("It must match the password you entered previously.");

        List<String> archetypes = Arrays.asList(
                "Collector", "Explorer", "Regular", "Reviewer", "Compulsive",
                "Strategist", "Socializer", "Achiever", "Speedrunner",
                "Minimalist", "Maximizer", "Risk-taker", "Perfectionist", "Pioneer"
        );
        segments.setItems(archetypes);
        segments.setPlaceholder("Choose...");
        segments.setClearButtonVisible(true);
        segments.setAutoExpand(MultiSelectComboBox.AutoExpandMode.BOTH);

        segments.setClassNameGenerator(item -> switch (item) {
            case "Collector" -> "collector-style";
            case "Explorer" -> "explorer-style";
            case "Regular" -> "regular-style";
            case "Reviewer" -> "reviewer-style";
            case "Compulsive" -> "compulsive-style";
            case "Strategist" -> "strategist-style";
            case "Socializer" -> "socializer-style";
            case "Achiever" -> "achiever-style";
            case "Speedrunner" -> "speedrunner-style";
            case "Minimalist" -> "minimalist-style";
            case "Maximizer" -> "maximizer-style";
            case "Risk-taker" -> "risk-taker-style";
            case "Perfectionist" -> "perfectionist-style";
            case "Pioneer" -> "pioneer-style";
            default -> "";
        });

        Footer footer = new Footer(createFooter());

        Scroller formScroller = scrollerVertical();
        formScroller.setContent(createFormLayout());
        formScroller.addClassNames(LumoUtility.Padding.MEDIUM);

        toggleGridButton.setWidthFull();
        searchField.setWidthFull();
        grid.setWidthFull();
        grid.setHeight("300px");
        VerticalLayout body = getVerticalLayout(formScroller, footer);

        add(body);
        setSizeFull();
        addClassNames(LumoUtility.Padding.MEDIUM);
    }


    private VerticalLayout getVerticalLayout(Scroller formScroller, Footer footer) {
        VerticalLayout headerLayout = new VerticalLayout(toggleGridButton, searchField, grid);
        headerLayout.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Padding.Top.XSMALL, LumoUtility.Padding.Bottom.XSMALL);
        headerLayout.setSpacing(false);

        Header header = new Header(headerLayout);

        VerticalLayout body = new VerticalLayout(header, formScroller, footer);
        body.setSizeFull();
        body.setPadding(false);
        body.addClassNames(LumoUtility.BoxShadow.XSMALL, LumoUtility.BorderRadius.LARGE);
        body.setJustifyContentMode(JustifyContentMode.BETWEEN);
        body.setAlignItems(Alignment.STRETCH);
        return body;
    }

    public Scroller scrollerVertical() {
        Scroller scroller = new Scroller();
        scroller.setSizeFull();
        scroller.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.NONE);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.addClassNames(LumoUtility.AlignContent.START);

        return scroller;
    }

    private HorizontalLayout createFormLayout() {
        H3 headerIdentity = new H3("Identity");
        H3 headerAddress = new H3("Address");
        headerAddress.addClassNames(LumoUtility.Margin.Top.LARGE);

        H3 headerChangePassword = new H3("Access info");
        headerChangePassword.addClassNames(LumoUtility.Margin.Top.LARGE);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames(LumoUtility.Padding.Right.XLARGE);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        HorizontalLayout cityStateZipLayout = new HorizontalLayout();
        cityStateZipLayout.setPadding(false);
        cityStateZipLayout.setMargin(false);
        cityStateZipLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        cityStateZipLayout.addClassNames(LumoUtility.FlexWrap.WRAP);
        cityStateZipLayout.add(city, state, zipCode);
        cityStateZipLayout.setFlexGrow(1, city);

        formLayout.add(headerIdentity, firstName, lastName, email, dateOfBirth, headerAddress, address, cityStateZipLayout, country, phone, headerChangePassword, username, new Div(), password, repeatPassword);
        formLayout.setColspan(headerIdentity, 2);
        formLayout.setColspan(headerAddress, 2);
        formLayout.setColspan(headerChangePassword, 2);
        formLayout.setColspan(address, 2);
        formLayout.setColspan(email, 2);
        formLayout.setColspan(cityStateZipLayout, 2);

        H3 headerStats = new H3("Archetypes and Stats");

        VerticalLayout verticallayout = new VerticalLayout(headerStats, segments, newsletter);
        verticallayout.addClassNames(LumoUtility.Padding.Left.NONE, LumoUtility.Padding.Top.NONE);
        verticallayout.addClassNames(LumoUtility.Padding.Right.XLARGE);
        verticallayout.setSpacing(false);
        segments.setWidthFull();

        newsletter.addClassNames(LumoUtility.Padding.Top.XLARGE);


        HorizontalLayout horizontallayout = new HorizontalLayout(formLayout, verticallayout);
        horizontallayout.setWidthFull();
        horizontallayout.setFlexGrow(1, verticallayout);
        horizontallayout.addClassName("archetypes-stats-layout");

        return horizontallayout;
    }

    private HorizontalLayout createFooter() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        newButton.addClassNames(LumoUtility.Margin.End.AUTO);

        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, newButton, deleteButton);
        buttonLayout.addClassNames(LumoUtility.FlexWrap.WRAP, LumoUtility.Padding.MEDIUM);
        buttonLayout.addClassNames(LumoUtility.Background.CONTRAST_10);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        return buttonLayout;
    }

    private void saveOrUpdate(ClickEvent<Button> buttonClickEvent) {
        try {

            if (element == null) {
                element = new Person();
            }

            binder.writeBean(this.element);

            var confirmDialog = CommonUtils.saveOrUpdateDialog(() -> {
                personService.update(this.element);

                deleteButton.setEnabled(false);
                element = null;
                binder.setBean(null);

                grid.getDataProvider().refreshAll();
            });

            confirmDialog.open();

        } catch (ObjectOptimisticLockingFailureException ex) {
            log.error(ex.getLocalizedMessage());
            NotificationUtils.error("Error updating the data. Somebody else has updated the record while you were making changes.");
        } catch (ValidationException ex) {
            log.error(ex.getLocalizedMessage());
            NotificationUtils.error(ex);
        }
    }

    private void delete(ClickEvent<Button> buttonClickEvent) {
        try {

            var confirmDialog = CommonUtils.deleteDialog(() -> {

                personService.delete(element.getId());

                element = null;
                binder.setBean(null);

                deleteButton.setEnabled(element != null);
                grid.getDataProvider().refreshAll();
            });

            confirmDialog.open();

        } catch (ObjectOptimisticLockingFailureException ex) {
            log.error(ex.getLocalizedMessage());
            NotificationUtils.error("Error updating the data. Somebody else has updated the record while you were making changes.");
        }
    }


}
