package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.service.UserService;

import java.io.IOException;

public class ControllerAuthentication {
    private UserService service;

    @FXML
    TextField FieldEmail;
    @FXML
    TextField FieldPassword;
    @FXML
    Label LabelFaild;
    @FXML
    Button ExitButton;
    @FXML
    Label FieldWelcome;


    private void Initialize(){
        //FieldWelcome.set;
    }



    private Integer LoginAux() {
        String email = FieldEmail.getText();
        String password = FieldPassword.getText();
        for (User x : service.getAll()) {
            if (x.getEmail().equals(email) && x.getPassword().equals(password)) {
                return x.getId().intValue();
            }
        }
        return -1;
    }

    public void LoginMain(ActionEvent actionEvent) throws IOException {
        if(FieldEmail!=null && FieldPassword!=null) {
            int id = LoginAux();
            User u;
            if (id != -1) {
                u = service.findOne(Integer.toString(id));
                UserMain(u);
            } else {
                LabelFaild.setText("Login failed!");
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Email or password null");
        }
    }

    private void UserMain(User u) throws IOException {
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UserMain.fxml"));
        AnchorPane root=fxmlLoader.load();

        ControllerUser cont = fxmlLoader.getController();
        Stage stageMain = (Stage) ExitButton.getScene().getWindow();
        cont.setValues(service,stageMain,u);

        Stage stage=new Stage();
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("User " + u.getFirstName());
        stage.getIcons().add(new Image("file:C:\\Users\\Timuc Iustin\\IdeaProjects\\Socialnetwork\\images\\user_image.jpg"));
        stageMain.close();
        stage.show();
    }

    public void setValues(UserService service) {
        this.service = service;
    }


    public void CancelField(ActionEvent actionEvent) {
        FieldPassword.clear();
        FieldEmail.clear();
    }

    public void CreateNewUser(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/NewUser.fxml"));
        AnchorPane root=fxmlLoader.load();

        ControllerNewUser cont = fxmlLoader.getController();
        Stage stageMain = (Stage) ExitButton.getScene().getWindow();
        cont.setValues(service,stageMain);

        Stage stage=new Stage();
        stage.setScene(new Scene(root, 550, 400));
        stage.setTitle("Creat new account");
        stage.getIcons().add(new Image("file:C:\\Users\\Timuc Iustin\\IdeaProjects\\Socialnetwork\\images\\Jack.jpg"));
        stageMain.close();
        stage.show();
    }

    public void Exit(ActionEvent actionEvent) {
        Stage stage = (Stage) ExitButton.getScene().getWindow();
        stage.close();
    }
}
