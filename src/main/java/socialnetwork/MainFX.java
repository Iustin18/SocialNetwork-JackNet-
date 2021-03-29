package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.controller.ControllerAuthentication;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.*;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.service.*;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Authentication.fxml"));
        AnchorPane root=loader.load();

        ControllerAuthentication cont = loader.getController();

        final String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        final String username= "postgres";
        final String password= "postgres";
        PagingRepository<Long, User> userRepositoryDataBase =
                new UserDataBaseRepository(url,username,password,new UserValidator());

        UserService userServiceDataBase= new UserService(userRepositoryDataBase);

        cont.setValues(userServiceDataBase);

        stage.setScene(new Scene(root, 570, 360));
        stage.setTitle("JackNet");
        stage.getIcons().add(new Image("file:C:\\Users\\Timuc Iustin\\IdeaProjects\\Socialnetwork\\images\\Jack.jpg"));
        stage.resizableProperty().setValue(false);
        stage.show();
    }

    public static void main(String[] args){ launch(args);}
}
