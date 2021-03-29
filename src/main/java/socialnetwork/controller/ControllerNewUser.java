package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.memory.RepositoryException;
import socialnetwork.service.ServiceException;
import socialnetwork.service.UserService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ControllerNewUser {
    private UserService service;
    private Stage s;
    private String imagePath;

    @FXML
    Button BackButton;
    @FXML
    Button ConfirmButton;

    @FXML
    TextField FirstNameField;
    @FXML
    TextField LastNameField;
    @FXML
    TextField EmailField;
    @FXML
    TextField PasswordField;

    @FXML
    ComboBox<String> YearCombo;
    @FXML
    ComboBox<String> MonthCombo;
    @FXML
    ComboBox<String> DayCombo;
    @FXML
    ComboBox<String> GenderCombo;

    @FXML
    Label LabelResult;

    @FXML
    ImageView ImageUser;

    private void InitializeYear(){
        ObservableList<String> list = FXCollections.observableArrayList();
        for(int i=1991;i<2020;i++)
            list.add(Integer.toString(i));
        YearCombo.setItems(list);
    }

    private void InitializeMonth(){
        ObservableList<String> list = FXCollections.observableArrayList();
        for(int i=1;i<12;i++)
            list.add(Integer.toString(i));
        MonthCombo.setItems(list);
    }

    private void InitializeDay(){
        ObservableList<String> list = FXCollections.observableArrayList();
        for(int i=1;i<31;i++)
            list.add(Integer.toString(i));
        DayCombo.setItems(list);
    }

    private void InitializeGender(){
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll("M");
        list.addAll("F");
        list.addAll("N");
        GenderCombo.setItems(list);
    }

    public void setValues(UserService userService, Stage s){
        this.service=userService;
        this.s=s;
        InitializeDay();
        InitializeGender();
        InitializeMonth();
        InitializeYear();
    }

    public void AddUser(ActionEvent actionEvent) {
        if(FirstNameField!=null && LastNameField!=null && EmailField!=null && PasswordField!=null
                && YearCombo.getValue()!=null && MonthCombo.getValue()!=null && DayCombo.getValue()!=null
                && GenderCombo.getValue()!=null) {
            String firstName = FirstNameField.getText();
            String lastName = LastNameField.getText();
            String email = EmailField.getText();
            String password = PasswordField.getText();
            String year = YearCombo.getValue();
            String month = MonthCombo.getValue();
            String day = DayCombo.getValue();
            String gender = GenderCombo.getValue();
            if (Integer.parseInt(month) < 10)
                month = "0" + month;
            if (Integer.parseInt((day)) < 10)
                day = "0" + day;
            System.out.println(firstName + " " + lastName + " "
                    + email + " " + password + " "
                    + year + " " + month + " " + day + " "
                    + gender);
            String image = null;
            if(imagePath!=null)
                image = imagePath;

            try {
                service.addUser(firstName, lastName, year + "-" + month + "-" + day, gender, email, password,image);
                LabelResult.setText("Added");
            } catch (ServiceException | RepositoryException | ValidationException e) {
                String[] tokens = e.toString().split("/");
                tokens[0] = tokens[0].split(":")[1];
                LabelResult.setText(tokens[0]);
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"You have to complete all the fields");
        }
    }

    public void Back(ActionEvent actionEvent) {
        s.show();
        Stage stageMain = (Stage) BackButton.getScene().getWindow();
        stageMain.close();
    }

    public void ChoseImage(MouseEvent mouseEvent) throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF Images", "jpg","gif");
        int returnVal = chooser.showOpenDialog(chooser.getParent());
        if(returnVal == JFileChooser.APPROVE_OPTION){
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
        }
        FileInputStream input = new FileInputStream(chooser.getSelectedFile().getPath());
        imagePath=chooser.getSelectedFile().getPath();
        Image image = new Image(input);
        ImageUser.setImage(image);
        System.out.println(imagePath);

    }
}
