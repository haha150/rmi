package org.inlm3.common;

import java.io.Serializable;

public interface NotificationDTO extends Serializable {

    public String getAction();
    public String getUser();
}
