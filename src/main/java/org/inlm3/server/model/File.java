package org.inlm3.server.model;

import org.inlm3.common.FileDTO;

import javax.persistence.*;

@Entity
@Table(name="File")
public class File implements FileDTO {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;

    @Column(name = "filename", nullable = false)
    private String fileName;

    @Column(name = "size", nullable = false)
    private String fileSize;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner", nullable = false)
    private User fileOwner;

    @Column(name = "permission", nullable = false)
    private String filePermission;

    public File() {}

    public File(String fileName, String fileSize, User fileOwner, String filePermission) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileOwner = fileOwner;
        this.filePermission = filePermission;
    }

    public long getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUsername() {
        return fileOwner.getUsername();
    }

    public String getFileSize() {
        return fileSize;
    }

    public User getFileOwner() {
        return fileOwner;
    }

    public String getFilePermission() {
        return filePermission;
    }
}
