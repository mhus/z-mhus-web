package de.mhus.cherry.editor.impl;

public interface AccessControl {

    public boolean signIn(String username, String password);

    public boolean isUserSignedIn();

    public boolean isUserInRole(String role);

    public String getPrincipalName();
}
