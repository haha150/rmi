package org.inlm3.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.inlm3.client.net.ReceiveFile;
import org.inlm3.client.view.View;
import org.inlm3.common.*;
import org.inlm3.server.exception.*;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

public class Controller {
    private static View view;
    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private FileCatalog fileCatalog;
    private UserDTO user;
    private File file;
    private List<FileDTO> files;
    private volatile boolean loggedIn;

    public Controller(View view, FileCatalog fc) {
        this.view = view;
        this.fileCatalog = fc;
        file = null;
        loggedIn = false;
    }

    public void showAlert(String message) {
        alert.setHeaderText("");
        alert.setTitle("Alert!");
        alert.setContentText(message);
        alert.show();
    }

    public void handleClose(WindowEvent event, Stage primaryStage) {
        loggedIn = false;
        event.consume();
        primaryStage.close();
    }

    public void handleButton(ActionEvent event, Stage primaryStage, Dialog dialog, TextField permission, TextField permission2, TextField nameField,
                             Dialog editDialog, CheckBox read, CheckBox write, CheckBox read2, CheckBox write2) {
        Button b = (Button) event.getSource();
        switch (b.getText()) {
            case "List files":
                try {
                    files = (List<FileDTO>) fileCatalog.listFiles(user.getUsername());
                    view.getData().clear();
                    for (FileDTO f : files) {
                        view.getData().add(f.getFileName() + ", Size: " + f.getFileSize() + ", Permission: " +
                                f.getFilePermission() + ", Owner: " + f.getUsername() + ", Read: " + f.isRead() + ", Write: "
                        + f.isWrite());
                    }
                } catch (RemoteException e) {
                    showAlert("Failed to communicate with server");
                }
                break;
            case "Download":
                System.out.println("Download files");
                String selectedItem = (String)view.getListView().getSelectionModel().getSelectedItem();
                if(selectedItem != null) {
                    File file = view.getFileSaver().showDialog(primaryStage);
                    if(file != null) {
                        try {
                            new Thread(() -> {
                                ReceiveFile fileTransfer = new ReceiveFile(Constants.CLIENT_PORT);
                                fileTransfer.receive(file.getAbsolutePath());
                            }).start();
                            fileCatalog.download(user.getUsername(), selectedItem.split(",")[0]);
                            showAlert("File downloaded!");
                        } catch (RemoteException e) {
                            showAlert("Failed to communicate with server");
                        } catch (PermissionDeniedException e) {
                            showAlert("Permission denied!");
                        } catch (FileDoesNotExistException e) {
                            showAlert("File does not exist");
                        }
                    }
                } else {
                    showAlert("Select a file from the list first");
                }
                break;
            case "Upload":
                dialog.showAndWait();
                if(file != null) {
                    try {
                        if(fileCatalog.upload(user.getUsername(),file.getName(),(int)file.length(),
                                permission.getText() != null ? permission.getText() : "public", read.isSelected(), write.isSelected())) {
                            FileTransfer fileTransfer = new FileTransfer("localhost", Constants.SERVER_PORT);
                            fileTransfer.send(file);
                        }
                    } catch (RemoteException e) {
                        showAlert("Failed to communicate with server");
                    } catch (FileAlreadyExistsException e) {
                        showAlert("File already exists!");
                    } catch (PermissionDeniedException e) {
                        showAlert("Permission denied!");
                    }
                }
                break;
            case "Edit":
                System.out.println("Edit files");
                String edit = (String)view.getListView().getSelectionModel().getSelectedItem();
                if(edit != null) {
                    String name = edit.split(",")[0];
                    FileDTO file = null;
                    for(FileDTO f : files) {
                        if(f.getFileName().equals(name)) {
                            file = f;
                            break;
                        }
                    }
                    if(file != null) {
                        nameField.setText(file.getFileName());
                        permission2.setText(file.getFilePermission());
                        read2.setSelected(file.isRead());
                        write2.setSelected(file.isWrite());
                        editDialog.showAndWait();
                        try {
                            fileCatalog.editFile(user.getUsername(), file.getFileName(), nameField.getText(),
                                    permission2.getText(), read2.isSelected(), write2.isSelected());
                        } catch (RemoteException e) {
                            showAlert("Failed to communicate with server");
                        } catch (PermissionDeniedException e) {
                            showAlert("Permission denied");
                        } catch (FileDoesNotExistException e) {
                            showAlert("File does not exist");
                        } catch (UserDoesNotExistException e) {
                            showAlert("User does not exist");
                        }
                    }
                } else {
                    showAlert("Select a file from the list first");
                }
                break;
            case "Delete":
                System.out.println("deleting files");
                String delete = (String)view.getListView().getSelectionModel().getSelectedItem();
                if(delete != null) {
                    String name = delete.split(",")[0];
                    try {
                        fileCatalog.deleteFile(user.getUsername(), name);
                    } catch (RemoteException e) {
                        showAlert("Failed to communicate with server");
                    } catch (FileDoesNotExistException e) {
                        showAlert("File does not exist.");
                    } catch (PermissionDeniedException e) {
                        showAlert("Permission denied.");
                    } catch (UserDoesNotExistException e) {
                        showAlert("User does not exist");
                    }
                }
                break;
            case "Notify":
                System.out.println("Notify files");
                String notify = (String)view.getListView().getSelectionModel().getSelectedItem();
                if(notify != null) {
                    String name = notify.split(",")[0];
                    try {
                        fileCatalog.notifyMe(user.getUsername(), name);
                    } catch (RemoteException e) {
                        showAlert("Failed to communicate with server");
                    }
                }
                break;
            default:
                break;
        }
    }

    public void loginHandler(Dialog dialog, TextField username, PasswordField password) {
        dialog.setTitle("Login");
        dialog.showAndWait();
        if(dialog.getResult().toString().contains("OK")) {
            if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                try {
                    user = fileCatalog.login(username.getText(), password.getText());
                    view.enableButtons();
                    loggedIn = true;
                    new Thread(new PollNotification()).start();
                    showAlert("You have logged in!");
                } catch (RemoteException e) {
                    showAlert("Failed to communicate with server");
                } catch (UserDoesNotExistException e) {
                    showAlert("User does not exist");
                } catch (WrongCredentialsException e) {
                    showAlert("Wrong username or password");
                }
            }
        }
    }

    public void logoutHandler() {
        try {
            fileCatalog.logout(user.getUsername());
        } catch (RemoteException e) {
            showAlert("Failed to communicate with server");
        }
        view.disableButtons();
        loggedIn = false;
        showAlert("Logged out!");
    }

    public void registerHandler(Dialog dialog, TextField username, PasswordField password) {
        dialog.setTitle("Register");
        dialog.showAndWait();
        if(dialog.getResult().toString().contains("OK")) {
            if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                try {
                    fileCatalog.register(username.getText(), password.getText());
                    showAlert("Successfully registered, you can now login!");
                } catch (RemoteException e) {
                    showAlert("Failed to communicate with server");
                } catch (UserAlreadyExistsException e) {
                    showAlert("User already exists");
                }
            }
        }
    }

    public void unregisterHandler(Dialog dialog, TextField username, PasswordField password) {
        dialog.setTitle("Unregister");
        dialog.showAndWait();
        if(dialog.getResult().toString().contains("OK")) {
            if(!username.getText().isEmpty() && !password.getText().isEmpty()) {
                try {
                    fileCatalog.unregister(username.getText(), password.getText());
                    showAlert("Successfully unregistered user!");
                } catch (RemoteException e) {
                    showAlert("Failed to communicate with server");
                } catch (UserDoesNotExistException e) {
                    showAlert("User does not exist");
                } catch (WrongCredentialsException e) {
                    showAlert("Wrong username or password");
                }
            }
        }
    }

    public void handleChoose(Stage primaryStage, FileChooser fileChooser) {
        file = fileChooser.showOpenDialog(primaryStage);
    }

    private class PollNotification implements Runnable {

        private final Alert alert = new Alert(Alert.AlertType.INFORMATION);

        @Override
        public void run() {
            while(loggedIn) {
                try {
                    StringBuilder sb = new StringBuilder();
                    List<NotificationDTO> notifications = (List<NotificationDTO>)fileCatalog.pollNotifications(user.getUsername());
                    if(notifications != null && !notifications.isEmpty()) {
                        for(NotificationDTO n : notifications) {
                            sb.append(n.getUser() + " did action: " + n.getAction() + "\n");
                        }
                        Platform.runLater(() -> {
                            showAlert(sb.toString());
                        });
                    }
                } catch (RemoteException e) {
                    showAlert("Failed to communicate with server");
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void showAlert(String message) {
            alert.setHeaderText("");
            alert.setTitle("Alert!");
            alert.setContentText(message);
            alert.show();
        }
    }
}