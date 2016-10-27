package de.mhus.cherry.editor.impl;


import java.io.Serializable;

import javax.security.auth.Subject;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.security.AccessControl;
import de.mhus.lib.core.security.MSecurity;
import de.mhus.lib.vaadin.VaadinAccessControl;

public class LoginScreen extends CssLayout {

	private static final long serialVersionUID = 1L;
	private static final String MAIN_USER_ROLE = "USER";
	private TextField username;
    private PasswordField password;
    private Button login;
//    private Button forgotPassword;
    private LoginListener loginListener;
    private AccessControl accessControl;

    public LoginScreen(AccessControl accessControl, LoginListener loginListener) {
        this.loginListener = loginListener;
        this.accessControl = accessControl;
        buildUI();
        username.focus();
    }

    private void buildUI() {
        addStyleName("login-screen");

        // login form, centered in the available part of the screen
        Component loginForm = buildLoginForm();

        // layout to center login form when there is sufficient screen space
        // - see the theme for how this is made responsive for various screen
        // sizes
        VerticalLayout centeringLayout = new VerticalLayout();
        centeringLayout.setStyleName("centering-layout");
        centeringLayout.addComponent(loginForm);
        centeringLayout.setComponentAlignment(loginForm,
                Alignment.MIDDLE_CENTER);

        // information text about logging in
        CssLayout loginInformation = buildLoginInformation();

        addComponent(centeringLayout);
        addComponent(loginInformation);
    }

    private Component buildLoginForm() {
        FormLayout loginForm = new FormLayout();

        loginForm.addStyleName("login-form");
        loginForm.setSizeUndefined();
        loginForm.setMargin(false);

        loginForm.addComponent(username = new TextField("Benutzername", ""));
        username.setWidth(15, Unit.EM);
        loginForm.addComponent(password = new PasswordField("Passwort"));
        password.setWidth(15, Unit.EM);
        password.setDescription("Dein Passwort");
        CssLayout buttons = new CssLayout();
        buttons.setStyleName("buttons");
        loginForm.addComponent(buttons);

        buttons.addComponent(login = new Button("Login"));
        login.setDisableOnClick(true);
        login.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    login();
                } finally {
                    login.setEnabled(true);
                }
            }
        });
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);

//        buttons.addComponent(forgotPassword = new Button("Forgot password?"));
//        forgotPassword.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent event) {
//                showNotification(new Notification("Hint: Try anything"));
//            }
//        });
//        forgotPassword.addStyleName(ValoTheme.BUTTON_LINK);
        return loginForm;
    }

    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        Label loginInfoText = new Label(
                "<h1>Magic Control Panel</h1>"
                        + "Bitte loggen Sie sich mit Ihrem Account ein.",
                ContentMode.HTML);
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }

    private void login() {
        if (accessControl.signIn(username.getValue(), password.getValue())) {
        	
        	Subject subject = (Subject)UI.getCurrent().getSession().getAttribute(VaadinAccessControl.SUBJECT_ATTR);
        	if (subject == null)
        		MLogUtil.log().i("no subject");
        	else
        		MLogUtil.log().i("Login",subject);
        	
        	if (MSecurity.hasRole(subject,MAIN_USER_ROLE)) {
        		loginListener.loginSuccessful();
        		return;
        	} else {
        		accessControl.signOut();
        	}
        }
        
        showNotification(new Notification("Login failed",
                "Please check your username and password and try again.",
                Notification.Type.HUMANIZED_MESSAGE));
        username.focus();

    }

    private void showNotification(Notification notification) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }

    public interface LoginListener extends Serializable {
        void loginSuccessful();
    }
}
