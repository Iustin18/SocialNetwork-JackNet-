package socialnetwork.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import com.itextpdf.layout.element.Paragraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.database.*;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.service.*;
import socialnetwork.utils.NotificationTypes;
import socialnetwork.utils.State;
import socialnetwork.utils.events.*;
import socialnetwork.utils.observer.Observer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ControllerUser implements Observer<ChangeEvent> {
    private UserService userService;
    private ChatService chatService;
    private MessageService messageService;
    private FriendshipService friendshipService;
    private NotificationService notificationService;
    private CalendarEventService calendarEventService;

    private Stage s;
    private User userMain;
    private Long id_Chat;
    private Long id_Message=null;
    private Long id_FriendRequest=null;
    private Long id_UserRequest=null;
    private Long id_Friend=null;
    private List<Long> participants = new ArrayList<>();
    private String imagePath;

    private int pageMessage=0;

    private NotifyEvents notifyEvents;

    ObservableList<Chat> chatsListObservable = FXCollections.observableArrayList();
    ObservableList<Friendship> friendshipsListObservable = FXCollections.observableArrayList();
    ObservableList<Message> messagesListObservable = FXCollections.observableArrayList();
    ObservableList<Notification> notificationListObservable = FXCollections.observableArrayList();
    ObservableList<User> usersListObservable = FXCollections.observableArrayList();
    ObservableList<String> friendsChooserListObservable = FXCollections.observableArrayList();
    ObservableList<CalendarEvent> calendarEventListObservable = FXCollections.observableArrayList();
    ObservableList<CalendarEvent> MyCalendarEventListObservable = FXCollections.observableArrayList();
    ObservableList<CalendarEvent> MyCalendarUmuteEventListObservable = FXCollections.observableArrayList();


//---------------------------JavaFx----------------------------------------------------

    @FXML
    TableView<Chat> ListChats;
    @FXML
    ListView<Message> ListMessages;
    @FXML
    ListView<Friendship> ListFriends;
    @FXML
    TableColumn<Chat,Long> idColumn;
    @FXML
    TableColumn<Chat,String> nameColumn;
    @FXML
    TableColumn<Chat,String> participantsColumn;
    @FXML
    Button BackButton;
    @FXML
    TextField Message;
    @FXML
    ComboBox<Notification> NotificationCombo;
    @FXML
    Button ButtonDec;
    @FXML
    Button ButtonAccept;
    @FXML
    Label LabelNotification;
    @FXML
    ComboBox<User> UsersCombo;
    @FXML
    Button DeleteNotification;
    @FXML
    Label firstNameField;
    @FXML
    Label lastNameField;
    @FXML
    Label birthdateField;
    @FXML
    Label MessageLabel;
    @FXML
    TextField NameChat;
    @FXML
    DatePicker StartDate;
    @FXML
    DatePicker FinishDate;
    @FXML
    ComboBox<String> FriendsChooser;
    @FXML
    TextField EventName;
    @FXML
    DatePicker EventDate;
    @FXML
    ListView<CalendarEvent> EventsList;
    @FXML
    ComboBox<String> HourCombo;
    @FXML
    ComboBox<String> MinCombo;
    @FXML
    ListView<CalendarEvent> MyEventsList;
    @FXML
    ImageView ImageUser;
    @FXML
    ImageView ChangePicture;
    @FXML
    TextField CurrentPasswor;
    @FXML
    TextField NewPassword;
    @FXML
    TextField FirstName;
    @FXML
    TextField LastName;

//--------------------------System------------------------------------------------------------

    public void setValues(UserService ser,Stage s,User u) throws FileNotFoundException {
        notifyEvents = new NotifyEvents(MyCalendarUmuteEventListObservable,this);

        this.userService =ser;
        this.s=s;
        this.userMain=u;
        InitializeService();
        Initialize();
        messageService.addObserver(this);
        notificationService.addObserver(this);
        userService.addObserver(this);
        friendshipService.addObserver(this);
        chatService.addObserver(this);
        calendarEventService.addObserver(this);
        initializeLabel();
        if(userMain.getImage()!=null)
            ImageUser.setImage(new Image(new FileInputStream(userMain.getImage())));
    }

    @Override
    public void update(ChangeEvent changeEvent) {
        userMain=userService.findOne(userMain.getId().toString());
        Initialize();
        try{
            initMessages();
        } catch (Exception ignored) {
        }
        initializeLabel();
        if(userMain.getImage()!=null) {
            try {
                ImageUser.setImage(new Image(new FileInputStream(userMain.getImage())));
            } catch (FileNotFoundException ignore) {
            }
        }
    }

    public void Refresh(ActionEvent actionEvent) {
        initMessages();
        InitializeMessage();
        Initialize();
    }

    public void Back(ActionEvent actionEvent) {
        notifyEvents.stop();
        Stage stage = (Stage) BackButton.getScene().getWindow();
        s.show();
        stage.close();
    }

    public void SetUserId(ActionEvent actionEvent) {
        User u=UsersCombo.getSelectionModel().getSelectedItem();
        if(u!=null)
            id_UserRequest=u.getId();
    }

//------------------------------------Initialize--------------------------------------------------

    private void initializeLabel(){
        firstNameField.setText(userMain.getFirstName());
        lastNameField.setText(userMain.getLastName());
        birthdateField.setText(userMain.getDate());
    }

    private void initMessages() {
        List<Message> list =  messageService.getMessageOnPage(pageMessage,id_Chat).stream()
                .sorted(Comparator.comparing(Entity::getId))
                .collect(Collectors.toList());
        messagesListObservable.setAll(list);
    }

    private void initEventsHM(){
        ObservableList<String> hour = FXCollections.observableArrayList();
        for(int i=0;i<25;i++){
            if(i<10)
                hour.add("0"+ i);
            else
                hour.add(Integer.toString(i));
        }
        HourCombo.setItems(hour);
        ObservableList<String> min = FXCollections.observableArrayList();
        for(int i=0;i<61;i++){
            if(i<10)
                min.add("0"+ i);
            else
                min.add(Integer.toString(i));
        }
        MinCombo.setItems(min);
    }

    private void initCalendarEvent(){
        List<CalendarEvent> list = new ArrayList<>(calendarEventService.getAll());
        calendarEventListObservable.setAll(list);
        if(calendarEventListObservable.size()!=0)
            notifyEvents.start();
    }

    private void initMyEventList(){
        List<CalendarEvent> list = new ArrayList<>(calendarEventService.getAllUser(userMain.getId()));
        MyCalendarEventListObservable.setAll(list);
        MyCalendarUmuteEventListObservable.setAll(list);
    }

    private void initUsers(){
        List<User> u = findUsers();
        usersListObservable.setAll(u);
        List<String> s = friendshipService.showFriendshipsWithId(userMain.getId());
        friendsChooserListObservable.setAll(s);
    }

    private void initNotification(){
        List<Notification> fin=notificationService.getAllUser(userMain.getId().intValue());
        notificationListObservable.setAll(fin);
        if(fin.size()!=0)
            LabelNotification.setVisible(true);
    }

    private void initFriends(){
        List<Friendship> list = friendshipService.getAll().stream()
                .filter(x-> x.getPair().getRight().equals(userMain.getId()) || x.getPair().getLeft().equals(userMain.getId()))
                .collect(Collectors.toList());
        friendshipsListObservable.setAll(list);
    }

    private void initChats(){
        List<Chat> list = chatService.getAll().stream()
                .filter(x->x.getParticipants().contains(userMain.getId()))
                .collect(Collectors.toList());
        chatsListObservable.setAll(list);
    }

    private void InitializeService(){
        final String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        final String username= "postgres";
        final String password= "postgres";

        PagingRepository<Long,Friendship> friendshipRepositoryDataBase =
                new FriendshipsDataBaseRepository(url,username,password,new FriendshipValidator());

        PagingRepository<Long, Chat> chatRepositoryDataBase =
                new ChatsDataBaseRepository(url,username,password,new ChatValidator());

        PagingRepository<Long, Message> messageRepositoryDataBase =
                new MessagesDataBaseRepository(url,username,password,new MessageValidator());

        PagingRepository<Long, socialnetwork.domain.Notification> friendRequestDataBaseRepository =
                new NotificationDataBaseRepository(url,username,password);

        PagingRepository<Long,User> userRepositoryDataBase =
                new UserDataBaseRepository(url,username,password,new UserValidator());

        PagingRepository<Long,CalendarEvent> calendarEventRepositoryDataBase =
                new CalendarEventDataBaseRepository(url,username,password,new CalendarEventValidator());

        messageService = new MessageService(messageRepositoryDataBase,chatRepositoryDataBase);
        chatService = new ChatService(chatRepositoryDataBase,userRepositoryDataBase);
        friendshipService = new FriendshipService(friendshipRepositoryDataBase,userRepositoryDataBase);
        notificationService = new NotificationService(friendRequestDataBaseRepository,userRepositoryDataBase);
        calendarEventService = new CalendarEventService(userRepositoryDataBase,calendarEventRepositoryDataBase);
    }

    private void InitializeChats(){
        initChats();
        ListChats.setItems(chatsListObservable);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        participantsColumn.setCellValueFactory(new PropertyValueFactory<>("participants"));
    }

    private void InitializeMessage(){
        ListMessages.setItems(messagesListObservable);
    }

    private List<User> findUsers(){
        List<Long> listFriends = friendshipService.getAll().stream()
                .filter(x->x.getPair().getLeft().equals(userMain.getId()) || x.getPair().getRight().equals(userMain.getId()))
                .map(x->{
                    Long id;
                    if(x.getPair().getLeft().equals(userMain.getId()))
                        return x.getPair().getRight();
                    else
                        return  x.getPair().getLeft();
                })
                .collect(Collectors.toList());
        return userService.getAll().stream().filter(x->
                !x.getId().equals(userMain.getId())
                        && !listFriends.contains(x.getId()))
                .collect(Collectors.toList());
    }

    private void Initialize(){
        InitializeChats();
        initUsers();
        initNotification();
        initFriends();
        initCalendarEvent();
        initMyEventList();
        initEventsHM();
        ListFriends.setItems(friendshipsListObservable);
        ListFriends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        NotificationCombo.setItems(notificationListObservable);
        UsersCombo.setItems(usersListObservable);
        FriendsChooser.setItems(friendsChooserListObservable);
        EventsList.setItems(calendarEventListObservable);
        MyEventsList.setItems(MyCalendarEventListObservable);
    }


//---------------------------Friend----------------------------------------------------------

    public void SetIdFriend(MouseEvent mouseEvent) {
        Friendship f = ListFriends.getSelectionModel().getSelectedItem();
        if(f!=null)
            id_Friend=f.getId();
        participants = ListFriends.getSelectionModel().getSelectedItems().stream()
                .map(x->{
                    if(!x.getPair().getLeft().equals(userMain.getId()))
                        return x.getPair().getLeft();
                    return x.getPair().getRight();
                }).collect(Collectors.toList());
    }

    public void DeleteFriend(ActionEvent actionEvent) {
        if(id_Friend!=null) {
            friendshipService.deleteFriendship(Long.toString(id_Friend));
            id_Friend = null;
        }
        else{
            MessageAlert.showErrorMessage(null,"You have to select a friend first");
        }
    }

//---------------------------PDF----------------------------------------------------

    public void FriendMessagePDF(ActionEvent actionEvent) throws ParseException, FileNotFoundException {
        if(StartDate.getValue()!=null && FinishDate.getValue()!=null) {
            List<String> listaMesaje = messageService.PeriodMessage(userMain.getId(), StartDate.getValue().toString(), FinishDate.getValue().toString());
            List<String> listFriends = friendshipService.PeriodFriends(userMain.getId(), StartDate.getValue().toString(), FinishDate.getValue().toString());

            String dest = "C:\\Users\\Timuc Iustin\\Desktop\\pdf Map\\MessageANdFriends.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();
            Document document = new Document(pdfDoc);

            Paragraph titleMessage = new Paragraph();
            titleMessage.add("Message from " + StartDate.getValue().toString() + " to " + FinishDate.getValue().toString());
            titleMessage.setBold();
            String listMessage = "";
            for (String s : listaMesaje) {
                listMessage = listMessage + s + '\n';
            }
            document.add(titleMessage);
            document.add(new Paragraph(listMessage));

            Paragraph titleFriends = new Paragraph();
            titleFriends.add("Friends from " + StartDate.getValue().toString() + " to " + FinishDate.getValue().toString());
            titleFriends.setBold();
            String friends = "";
            for (String s : listFriends)
                friends = friends + s + '\n';
            document.add(titleFriends);
            document.add(new Paragraph(friends));

            document.close();
        }
        else{
            MessageAlert.showErrorMessage(null,"You must select the period");
        }
    }

    public void MessagePdf(ActionEvent actionEvent) throws ParseException, FileNotFoundException {
        if(FriendsChooser.getValue()!=null && StartDate.getValue()!=null && FinishDate.getValue()!=null) {
            Long id_friend = Long.parseLong(FriendsChooser.getValue().split(" ")[1]);
            List<String> list = messageService.PeriodMessage(userMain.getId(),id_friend,StartDate.getValue().toString(),FinishDate.getValue().toString());


            String dest = "C:\\Users\\Timuc Iustin\\Desktop\\pdf Map\\Message.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();
            Document document = new Document(pdfDoc);
            Paragraph titleMessage = new Paragraph();
            titleMessage.add("Message from " + StartDate.getValue().toString() + " to " + FinishDate.getValue().toString());
            titleMessage.setBold();
            String listMessage = "";
            for(String s : list){
                listMessage=listMessage + s + '\n';
            }
            document.add(titleMessage);
            document.add(new Paragraph(listMessage));
            document.close();
        }
        else{
            MessageAlert.showErrorMessage(null,"You must select the period and the friend!");
        }
    }

//---------------------------Message----------------------------------------------------

    public void SendMessage(KeyEvent keyEvent) {
        if(id_Chat!=null) {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                if (id_Message == null) {
                    messageService.addMessage(id_Chat.toString(), Message.getText(), userMain.getId().toString());
                } else {
                    messageService.addMessage(id_Chat.toString(), Message.getText(), id_Message.toString(), userMain.getId().toString());
                    id_Message = null;
                }
                Message.clear();
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"You have to select a chat first");
        }
    }

    public void SetReplay(MouseEvent mouseEvent) {
        Message aux = ListMessages.getSelectionModel().getSelectedItem();
        if(aux!=null)
            id_Message = aux.getId();
    }

//---------------------------Chat----------------------------------------------------

    public void CreateAChat(ActionEvent actionEvent) {
        if(participants.size()!=0){
            ArrayList<Long> part=new ArrayList<>();
            part.add(userMain.getId());
            part.addAll(participants);
            try {
                chatService.AddChat("Chat", part);
            } catch (Exception e) {
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }else{
            MessageAlert.showErrorMessage(null,"Select at list one friend!");
        }
    }

    public void RowClick(MouseEvent mouseEvent) {
        try{
            id_Chat = ListChats.getSelectionModel().getSelectedItem().getId();
            if (id_Chat != null) {
                MessageLabel.setText(chatService.getOne(id_Chat).getName());
                initMessages();
                InitializeMessage();
            }
        } catch (Exception ignored) {
        }
    }

    public void SetNameChat(ActionEvent actionEvent) {
        if(id_Chat!=null){
            if(!NameChat.getText().equals("")){
                Chat aux= chatService.getOne(id_Chat);
                aux.setName(NameChat.getText());
                chatService.modify(aux);
                NameChat.clear();
            }
            else
                MessageAlert.showErrorMessage(null,"A Name must be write");
        }else
            MessageAlert.showErrorMessage(null, "A chat must be selected!");
    }

    public void DeleteChat(ActionEvent actionEvent) {
        if(id_Chat!=null){
            chatService.delete(id_Chat);
        }
        else
            MessageAlert.showErrorMessage(null,"A chat must be selected!");
    }

//---------------------------Notification----------------------------------------------------

    public void SendFriendRequest(ActionEvent actionEvent) {
        if(id_UserRequest!=null) {
            notificationService.add(userMain.getId().toString(), id_UserRequest.toString(), NotificationTypes.FRIENDREQUEST,null);
            id_UserRequest = null;
            friendshipsListObservable.remove(ListFriends.getSelectionModel().getSelectedItem());
            ListFriends.setItems(friendshipsListObservable);
        }
        else{
            MessageAlert.showErrorMessage(null,"You have to select a user first");
        }
    }

    public void DeleteNotification(ActionEvent actionEvent) {
        notificationService.delte(id_FriendRequest);
        LabelNotification.setVisible(false);
        DeleteNotification.setVisible(false);
    }

    public void ShowButtons(ActionEvent actionEvent) {
        Notification f = NotificationCombo.getSelectionModel().getSelectedItem();
        if(f!=null) {
            if(f.getId_sender()!=userMain.getId()) {
                ButtonAccept.setVisible(true);
                ButtonDec.setVisible(true);
            }
            else{
                DeleteNotification.setVisible(true);
            }
            id_FriendRequest = f.getId();
        }
    }

    private void FriendRequestHandel(String s){
        socialnetwork.domain.Notification f = notificationService.getOne(id_FriendRequest.toString());
        if(s.equals("a")){
            f.setState(State.accepted);
            friendshipService.addFriendship(f.getId_sender().toString(),f.getId_receiver().toString());
        }
        else{
            f.setState(State.declined);
        }
        notificationService.delte(f.getId());
        ButtonAccept.setVisible(false);
        ButtonDec.setVisible(false);
        LabelNotification.setVisible(false);
    }

    public void AcceptNotification(ActionEvent actionEvent) {
        FriendRequestHandel("a");
    }

    public void DeclineNotification(ActionEvent actionEvent) {
        FriendRequestHandel("d");
    }

    public void GetTimeNotification() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatterD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String time = LocalTime.now().format(formatter);
        String date = LocalDate.now().format(formatterD);

        if(MyCalendarEventListObservable.size()==0) {
            notifyEvents.stop();
            return;
        }

        for (CalendarEvent c : MyCalendarEventListObservable) {
            Tuple<Long, Long> data = new Tuple<>(-2L,-2L);
            try {
                data = FindTime(c, time, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(data.getRight()==-1L && data.getLeft()==-1L) {
                calendarEventService.delete(c.getId());
                notifyEvents.stop();
                break;
            }

            if(data.getLeft()==0L && data.getRight()==0L){
                String rez = "The Event: " + c.getName() + " will start NOW!!!!";
                notificationService.add(userMain.getId().toString(),null,NotificationTypes.REMINDER,rez);
            }

            if(data.getRight()/60==1) {
                String rez = "The Event: " + c.getName() + " will start in 1 hour";
                notificationService.add(userMain.getId().toString(),null,NotificationTypes.REMINDER,rez);
            }

            if(data.getRight()%60==30 || data.getRight()%60==15 || data.getRight()%60==10 || data.getRight()%60==5 || data.getRight()%60==1) {
                String rez = "The Event: " + c.getName() + " will start in " + data.getRight()%60 + " minutes";
                notificationService.add(userMain.getId().toString(),null,NotificationTypes.REMINDER,rez);
            }
        }
    }

//---------------------------Events----------------------------------------------------

    public void CreateEvent(ActionEvent actionEvent) throws ValidationException {
        if(EventName.getText() == null || EventDate.getValue() == null || HourCombo.getValue()==null || MinCombo.getValue()==null){
            MessageAlert.showErrorMessage(null,"Name and date can't be null");
        }
        else{
            String time = HourCombo.getValue()+ ":" + MinCombo.getValue();
            try {
                calendarEventService.AddCalendarEvent(EventName.getText(), EventDate.getValue().toString(), time, null,userMain.getId());
            } catch (ValidationException e) {
                String message = e.toString().split(":")[1];
                MessageAlert.showErrorMessage(null,message);
            }
        }
    }

    public void DeleteEvent(ActionEvent actionEvent) {
        if (EventsList.getSelectionModel().getSelectedItem() == null)
            MessageAlert.showErrorMessage(null, "Select an event first");
        CalendarEvent c = EventsList.getSelectionModel().getSelectedItem();
        try {
            if(c.getCreator()!=userMain.getId())
                MessageAlert.showErrorMessage(null, "You are not the creator");
            else
                calendarEventService.delete(c.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SubmitEvent(ActionEvent actionEvent){
        if (EventsList.getSelectionModel().getSelectedItem() == null)
            MessageAlert.showErrorMessage(null, "Select an event first");
        else {
            CalendarEvent c = EventsList.getSelectionModel().getSelectedItem();
            try {
                if(c.getParticipants()!=null && c.getParticipants().contains(userMain.getId()))
                    MessageAlert.showErrorMessage(null, "You already submit");
                else
                    calendarEventService.update(c, userMain.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Tuple<Long,Long> FindTime(CalendarEvent c,String time,String date) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date firstDate = sdf.parse(c.getDate());
        Date secondDate = sdf.parse(date);

        Long aux = firstDate.getTime()-secondDate.getTime();
        if(aux<0)
            return new Tuple<>(-1L,-1L);
        Long difD = TimeUnit.DAYS.convert(aux,TimeUnit.MINUTES)/60000;

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        Date time1 = sdf2.parse(c.getTime());
        Date time2 = sdf2.parse(time);
        if(time1.getTime()<time2.getTime()) {
            if(difD<=0)
                return new Tuple<>(-1L,-1L);
            time1.setTime(time1.getTime() + 86400000);
            difD=difD-1;
        }
        Long difT = Math.abs(time2.getTime()-time1.getTime())/1000/60;

        Tuple<Long,Long> rez=new Tuple<>(0L,0L);

        rez.setLeft(difD);
        rez.setRight(difT);

        return rez;
    }

    public void DeleteMyEvent(ActionEvent actionEvent) {
        if(MyEventsList.getSelectionModel().getSelectedItem()==null)
            MessageAlert.showErrorMessage(null,"Select first a event");
        else{
            CalendarEvent c = MyEventsList.getSelectionModel().getSelectedItem();
            calendarEventService.updateRevers(c,userMain.getId());
        }
    }

    public void MuteEvent(ActionEvent actionEvent) {
        if (MyEventsList.getSelectionModel().getSelectedItem() == null)
            MessageAlert.showErrorMessage(null, "Select an event first");
        CalendarEvent c = MyEventsList.getSelectionModel().getSelectedItem();
        MyCalendarUmuteEventListObservable.remove(c);
    }

//------------------------------Message paginate--------------------------------

    public void PreviousMessages(ActionEvent actionEvent) {
        if(pageMessage>0) {
            pageMessage--;
            List<Message> list = messageService.getMessageOnPage(pageMessage,id_Chat).stream()
                    .sorted(Comparator.comparing(Entity::getId))
                    .collect(Collectors.toList());
            messagesListObservable.setAll(list);
        }
        ListMessages.setItems(messagesListObservable);
    }

    public void NextMessages(ActionEvent actionEvent) {
        pageMessage++;
        List<Message> list = messageService.getMessageOnPage(pageMessage, id_Chat).stream()
                .sorted(Comparator.comparing(Entity::getId))
                .collect(Collectors.toList());
        messagesListObservable.setAll(list);
        ListMessages.setItems(messagesListObservable);
    }

//------------------------------Settings--------------------------------

    public void ChangeData(MouseEvent mouseEvent) {
        String firstname=userMain.getFirstName();
        String lastname=userMain.getLastName();
        String image=userMain.getImage();
        String password=userMain.getPassword();

        if(!FirstName.getText().equals("") && !LastName.getText().equals("") && imagePath!=null && !CurrentPasswor.getText().equals("") && !NewPassword.getText().equals(""))
            return;

        if(!FirstName.getText().equals("")) {
            firstname = FirstName.getText();
        }
        if(!LastName.getText().equals("")) {
            lastname = LastName.getText();
        }
        if(imagePath!=null) {
            image = imagePath;
        }
        if(!CurrentPasswor.getText().equals("") && !NewPassword.getText().equals("")) {
            if (CurrentPasswor.getText().equals(userMain.getPassword()))
                password = NewPassword.getText();
            else
                MessageAlert.showErrorMessage(null, "Wrong current password!");
        }
        boolean t = userService.modify(userMain.getId(),firstname,lastname,userMain.getDate(),userMain.getGender(),userMain.getEmail(),password,image);
        System.out.println(image);
        if(t)
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Changing","Change succeed");
        else
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Changing","Change failed");
    }

    public void ChangePhoto(MouseEvent mouseEvent) throws FileNotFoundException {
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
        ChangePicture.setImage(image);
    }
}


