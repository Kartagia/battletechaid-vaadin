package com.kautiainen.antti.btechgame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout implements ComponentEventListener<ClickEvent<MenuItem>> {

    /**
     * The logger logging for main view.
     */
    static final Logger log = LoggerFactory.getLogger(MainView.class);

    public static SubMenu createCampagnMenu(MenuBar parent, ComponentEventListener<ClickEvent<MenuItem>> listener) {
        MenuItem campaign = parent.addItem("Campaign");
        SubMenu campaignMenu = campaign.getSubMenu();
        MenuItem create = campaignMenu.addItem("Create");
        create.addClickListener(listener);
        create.setId("campaign-create");
        MenuItem open = campaignMenu.addItem("Open");
        open.setId("campaign-open");
        open.addClickListener(listener);
        MenuItem close = campaignMenu.addItem("Close");
        close.addClickListener(listener);
        close.setId("campaign-close");
        return campaignMenu;
    }

    public static SubMenu createAccountMenu(MenuBar parent, ComponentEventListener<ClickEvent<MenuItem>> listener) {
        MenuItem account = parent.addItem("Account");
        SubMenu accountMenu = account.getSubMenu();
        return accountMenu;
    }

    public static MenuBar createMenuBar(ComponentEventListener<ClickEvent<MenuItem>> listener) {
        MenuBar menuBar = new MenuBar();
        createCampagnMenu(menuBar, listener);
        createAccountMenu(menuBar, listener);
        return menuBar;
    }

    @SuppressWarnings("unsafe")
    public MainView() {
        MenuBar menuBar = createMenuBar(this);
        add(menuBar);

        Button button = new Button("Click me",
                event -> add(new Paragraph("Clicked!")));

        add(button);
    }

    @Override
    public void onComponentEvent(ClickEvent<MenuItem> event) {
        MenuItem item = event.getSource();
        switch (item.getId().orElse("")) {
            case "campaign-create" -> performCreateCampaign(event);
            case "campaign-open" -> performOpenCampaign(event);
            case "campaign-close" -> performCloseCampaign(event);
            case "account-create" -> performSignIn(event);
            case "account-select" -> performLogIn(event);
            case "account-close" -> performLogOut(event);
            default -> log.atError().log("Unknown menu item [%s]:%s", item.getId(), item.getText());
        }
;
    }

    /**
     * Has the user logged in.
     * @return True, if and only if the user has logged in.
     */
    protected boolean isLogged() {
        return true;
    }

    private void performCreateCampaign(ClickEvent<MenuItem> event) {
        if (isLogged()) {
            event.getSource().getUI().ifPresent( ui -> ui.navigate("/users/campaign/create"));
        } else {
            event.getSource().setEnabled(false);
        }
    }

    private void performOpenCampaign(ClickEvent<MenuItem> event) {
        if (isLogged()) {
            event.getSource().getUI().ifPresent( ui -> ui.navigate("/users/campaign/show"));
        } else {
            event.getSource().setEnabled(false);
        }
    }

    private void performCloseCampaign(ClickEvent<MenuItem> event) {
        if (isLogged()) {
            event.getSource().getUI().ifPresent( ui -> ui.navigate("/"));
        } else {
            event.getSource().setEnabled(false);
        }
    }

    private void performSignIn(ClickEvent<MenuItem> event) {
        if (isLogged()) {
            event.getSource().getUI().ifPresent( ui -> ui.navigate("/signup"));
        } else {
            event.getSource().setEnabled(false);
        }
    }

    private void performLogIn(ClickEvent<MenuItem> event) {
        if (isLogged()) {
            event.getSource().getUI().ifPresent( ui -> ui.navigate("/login"));
        } else {
            event.getSource().setEnabled(false);
        }
    }

    private void performLogOut(ClickEvent<MenuItem> event) {
        if (isLogged()) {
            event.getSource().getUI().ifPresent( ui -> ui.navigate("/logout"));
        } else {
            event.getSource().setEnabled(false);
        }
    }
}
