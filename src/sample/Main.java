package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {


    /*HOW TO RUN PROGRAMM?
    * 1) Run prog.
    * 2) Change "isServer" to "false"
    * 3) Run prog.
    * 4) Profit!!!
    * */
    private boolean isServer = false;  // Change this for create Server - true, Client - false

    private TextArea message = new TextArea();
    private NetWorkConnection connection = isServer ? createServer() : createClient();

    private Parent createContent(){
        message.setPrefHeight(550);
        TextField input = new TextField();
        input.setOnAction(event -> {
            String messege = isServer ? "Server: " : "Client: ";
            messege += input.getText();
            input.clear();

            message.appendText(messege + "\n");
            try {
                connection.send(messege);
            } catch (Exception e) {
                message.appendText("Faild to send!\n");
            }
        });
        VBox root = new VBox(20,message,input);
        root.setPrefSize(600, 600);
        return root;
    }

    public void init() throws Exception{
        connection.StartConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();

    }

    public void stop() throws Exception{
        connection.closeConnection();
    }

    private Server createServer(){
        return new Server(55555, data -> {
            Platform.runLater(() -> {
                message.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createClient(){
        return new Client("127.0.0.1", 55555, data-> {
            Platform.runLater(() -> {
                message.appendText(data.toString() + "\n");
            });
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
