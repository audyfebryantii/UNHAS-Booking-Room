package projek.projekpbo;

//Import tools yang dibutuhkan
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    private Stage stage;
    private Scene scene;

    //Tombol untuk menuju menu login admin
    public void switchTologinAdmin(ActionEvent event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("loginAdmin.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        Image icon = new Image("D:\\Coding\\Sem 3\\Java\\UNHAS-booking-room-main\\src\\main\\resources\\projek\\images\\Logo-Resmi-Unhas-1.png");
        stage.getIcons().add(icon);
        stage.setTitle("Room Booking Portal Universitas Hasanuddin");
        stage.setScene(scene);
        stage.show();
    }

    //Tombol untuk menuju menu login mahasiswa
    public void switchToDataStudent(ActionEvent event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("dataStudent.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}