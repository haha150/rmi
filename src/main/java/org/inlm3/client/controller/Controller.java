package org.inlm3.client.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.inlm3.client.view.View;
import org.inlm3.common.Constants;
import org.inlm3.common.FileCatalog;
import org.inlm3.common.FileTransfer;
import org.inlm3.common.UserDTO;
import org.inlm3.server.exception.*;

import java.rmi.RemoteException;

public class Controller {
    private static View view;
    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private FileCatalog fileCatalog;
    private UserDTO user;

    public Controller(View view, FileCatalog fc) {
        this.view = view;
        this.fileCatalog = fc;
    }


    public void showAlert(String message) {
        alert.setHeaderText("");
        alert.setTitle("Alert!");
        alert.setContentText(message);
        alert.show();
    }

    public void handleClose(WindowEvent event, Stage primaryStage) {
        event.consume();
        primaryStage.close();
    }

    public void handleButton(ActionEvent event) {
        Button b = (Button) event.getSource();
        switch (b.getText()) {
            case "List files":
                System.out.println("list files");
                break;
            case "Download":
                System.out.println("Download files");
                break;
            case "Upload":
                try {
                    if(fileCatalog.upload(user.getUsername(),"steam.txt",42,"public")) {
                        FileTransfer fileTransfer = new FileTransfer("localhost", Constants.SERVER_PORT);
                        fileTransfer.send("D:\\Downloads\\steam.txt");
                    }
                } catch (RemoteException e) {
                    showAlert("Failed to communicate with server");
                } catch (FileAlreadyExistsException e) {
                    showAlert("File already exists!");
                } catch (PermissionDeniedException e) {
                    showAlert("Permission denied!");
                }
                break;
            case "Edit":
                System.out.println("Edit files");
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
        view.disableButtons();
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
}