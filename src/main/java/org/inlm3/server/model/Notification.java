package org.inlm3.server.model;

import org.inlm3.common.NotificationDTO;

public class Notification implements NotificationDTO {

    private String action;
    private String user;

    public Notification(String action, String user) {
        this.action = action;
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public String getUser() {
        return user;
    }
}
