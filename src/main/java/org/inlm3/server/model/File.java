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
    private int fileSize;

    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "owner", nullable = false)
    private User fileOwner;

    @Column(name = "permission", nullable = false)
    private String filePermission;

    public File() {}

    public File(String fileName, int fileSize, User fileOwner, String filePermission) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileOwner = fileOwner;
        this.filePermission = filePermission;
    }

    public long getFileId() {
        return fileId;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getUsername() {
        return fileOwner.getUsername();
    }

    @Override
    public int getFileSize() {
        return fileSize;
    }

    public User getFileOwner() {
        return fileOwner;
    }

    @Override
    public String getFilePermission() {
        return filePermission;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileOwner(User fileOwner) {
        this.fileOwner = fileOwner;
    }

    public void setFilePermission(String filePermission) {
        this.filePermission = filePermission;
    }
}
