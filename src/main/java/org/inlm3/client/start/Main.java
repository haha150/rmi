package org.inlm3.client.start;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.inlm3.client.controller.Controller;
import org.inlm3.client.view.View;
import org.inlm3.common.FileCatalog;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main extends Application {

    /**
     * Create the view and scene and start the program
     * @param primaryStage the specified stage
     */
    @Override
    public void start(Stage primaryStage) {
        View view = new View(primaryStage);
        Scene scene = new Scene(view, 550, 400);
        FileCatalog fc = null;

        try {
            fc = (FileCatalog) Naming.lookup("server");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            System.out.println("failed to start file catalog client");
            System.exit(0);
        }

        Controller controller = new Controller(view, fc);
        view.addEventHandlers(controller);

        primaryStage.setTitle("File catalog");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}