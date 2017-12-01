package org.inlm3.common;

import java.io.Serializable;

public interface FileDTO extends Serializable {

    public String getFileName();
    public String getUsername();
    public int getFileSize();
    public String getFilePermission();
}
