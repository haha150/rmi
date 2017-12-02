package org.inlm3.client.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.inlm3.client.controller.Controller;

import java.util.ArrayList;
import java.util.List;

public class View extends BorderPane {
    private final Stage primaryStage;
    private final ObservableList<String> data;
    private ListView listView;
    private List<Button> buttons;
    private Dialog dialog;
    private Dialog uploadDialog;
    private Dialog editDialog;
    private TextField username;
    private TextField permission;
    private TextField permission2;
    private TextField name;
    private PasswordField password;
    private MenuItem login;
    private MenuItem logout;
    private MenuItem register;
    private MenuItem unregister;
    private Button chooseFile;
    private FileChooser fileChooser;
    private DirectoryChooser fileSaver;

    public View(Stage primaryStage) {
        this.primaryStage = primaryStage;
        data = FXCollections.observableArrayList();;
        buttons = new ArrayList<>();
        initView();
    }

    public ObservableList<String> getData() {
        return data;
    }

    public ListView getListView() {
        return listView;
    }

    public DirectoryChooser getFileSaver() {
        return fileSaver;
    }

    private void initView() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Server");
        login = new MenuItem("Login");
        logout = new MenuItem("Logout");
        register = new MenuItem("Register");
        unregister = new MenuItem("Unregister");
        menu.getItems().addAll(login,logout,register,unregister);
        menuBar.getMenus().add(menu);

        listView = new ListView();
        listView.setItems(data);

        HBox hbox = new HBox();
        hbox.setSpacing(10);

        Button listFiles = new Button();
        listFiles.setText("List files");
        listFiles.setDisable(true);
        buttons.add(listFiles);

        Button download = new Button();
        download.setText("Download");
        download.setDisable(true);
        buttons.add(download);

        Button upload = new Button();
        upload.setText("Upload");
        upload.setDisable(true);
        buttons.add(upload);

        Button edit = new Button();
        edit.setText("Edit");
        edit.setDisable(true);
        buttons.add(edit);

        Button delete = new Button();
        delete.setText("Delete");
        delete.setDisable(true);
        buttons.add(delete);

        Button notify = new Button();
        notify.setText("Notify");
        notify.setDisable(true);
        buttons.add(notify);

        hbox.getChildren().addAll(listFiles, download, upload, edit, delete, notify);

        fileSaver = new DirectoryChooser();
        fileSaver.setTitle("Select file destination");

        this.setTop(menuBar);
        this.setCenter(listView);
        this.setBottom(hbox);
        initDialogView();
        initUploadDialogView();
        initEditDialogView();
    }

    private void initDialogView() {
        VBox container = new VBox();

        Label usr = new Label();
        usr.setText("Username:");
        username = new TextField();
        username.setText("abc");   // tmp
        Label pass = new Label();
        pass.setText("Password:");
        password = new PasswordField();
        password.setText("abcd");  // tmp
        password.setPromptText("Enter password");

        container.getChildren().addAll(usr,username,pass,password);

        dialog = new Dialog();
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(container);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
    }

    private void initUploadDialogView() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        VBox container = new VBox();

        Label perm = new Label();
        perm.setText("Permission:");
        permission = new TextField();
        permission.setText("public");
        Label file = new Label();
        file.setText("File:");
        chooseFile = new Button();
        chooseFile.setText("Choose file");

        container.getChildren().addAll(perm,permission,file,chooseFile);

        uploadDialog = new Dialog();
        uploadDialog.setResizable(false);
        uploadDialog.setTitle("File chooser");
        uploadDialog.getDialogPane().setContent(container);
        uploadDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
    }

    private void initEditDialogView() {
        VBox container = new VBox();

        Label file = new Label();
        file.setText("Name:");
        name = new TextField();
        Label perm = new Label();
        perm.setText("Permission:");
        permission2 = new TextField();

        container.getChildren().addAll(file,name,perm,permission2);

        editDialog = new Dialog();
        editDialog.setResizable(false);
        editDialog.setTitle("Edit file");
        editDialog.getDialogPane().setContent(container);
        editDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
    }

    public void enableButtons() {
        for(Button b : buttons) {
            b.setDisable(false);
        }
    }

    public void disableButtons() {
        for(Button b : buttons) {
            b.setDisable(true);
        }
    }

    public void addEventHandlers(Controller controller) {
        EventHandler<WindowEvent> closeHandler = event -> controller.handleClose(event, primaryStage);
        primaryStage.setOnCloseRequest(closeHandler);

        EventHandler<ActionEvent> buttonHandler = event -> controller.handleButton(event, primaryStage, uploadDialog, permission, permission2, name, editDialog);

        for(Button b : buttons) {
            b.setOnAction(buttonHandler);
        }

        EventHandler<ActionEvent> chooseHandler = event -> controller.handleChoose(primaryStage,fileChooser);
        chooseFile.setOnAction(chooseHandler);

        EventHandler<ActionEvent> loginHandler = event -> controller.loginHandler(dialog, username, password);
        login.setOnAction(loginHandler);

        EventHandler<ActionEvent> logoutHandler = event -> controller.logoutHandler();
        logout.setOnAction(logoutHandler);

        EventHandler<ActionEvent> registerHandler = event -> controller.registerHandler(dialog, username, password);
        register.setOnAction(registerHandler);

        EventHandler<ActionEvent> unregisterHandler = event -> controller.unregisterHandler(dialog, username, password);
        unregister.setOnAction(unregisterHandler);
    }
}
