package socialnetwork.ui;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.memory.RepositoryException;
import socialnetwork.service.*;
import socialnetwork.utils.NotificationTypes;
import socialnetwork.utils.State;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UserUi {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final Scanner in = new Scanner(System.in);

    public UserUi(UserService userService, FriendshipService friendshipService, ChatService chatService, MessageService messageService, NotificationService notificationService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.chatService = chatService;
        this.messageService = messageService;
        this.notificationService = notificationService;
    }

    /**
     * Print the menu
     */
    private void menuUser(int x){
        List<User> user = userService.getAll().stream().filter(u->u.getId().intValue()==x).collect(Collectors.toList());
        User u = user.get(0);
        System.out.println("user: " + u.getFirstName());
        System.out.println("        ~~Menu~~");
        System.out.println("1)Notification");
        System.out.println("2)Chats");
        System.out.println("3)See all the users");
        System.out.println("4)Add friendship");
        System.out.println("5)Delete friendship");
        System.out.println("6)See all the friendships");
        System.out.println("7)Friendships in the month specified");
        System.out.println("8)displays messages between two users");
        System.out.println("9)Delete profile");
        System.out.println("0)Exit");
        System.out.println("insert the number of the desired option: ");
    }

    private void menuAdmin(){
        System.out.println("Admin");
        System.out.println("        ~~Menu~~");
        System.out.println("1)See all the users");
        System.out.println("2)See all the friendships");
        System.out.println("3)Number of communities");
        System.out.println("4)The most sociable community");
        System.out.println("5)Add a user");
        System.out.println("6)Delete a user");
        System.out.println("7)Add a friendship");
        System.out.println("8)Delete a friendship");
        System.out.println("9)Displays messages between two users");
        System.out.println("0)Exit");
        System.out.println("insert the number of the desired option: ");
    }

    private void menu(){
        System.out.println("        ~~Menu~~");
        System.out.println("1) Login");
        System.out.println("2) New User");
        System.out.println("0) Exit");
        System.out.println("insert the number of the desired option: ");
    }

    private void menuChat(){
        System.out.println("        ~~Menu~~");
        System.out.println("1)Your chats");
        System.out.println("2)New chat");
        System.out.println("3)Message to");
        System.out.println("0)Exit");
        System.out.println("insert the number of the desired option: ");
    }

    private void menuNotification(){
        System.out.println("        ~~Menu~~");
        System.out.println("1)Display notification");
        System.out.println("2)Respond to notification");
        System.out.println("0)Exit");
    }

    private int login(){
        String email,password;
        System.out.println("Email: ");
        email=in.nextLine();
        System.out.println("Password: ");
        password=in.nextLine();
        if(email.equals("admin") && password.equals("admin"))
            return 0;
        for(User x : userService.getAll()) {
            if (x.getEmail().equals(email) && x.getPassword().equals(password))
                return x.getId().intValue();
        }
        return -1;
    }

//--------------------------------------------------------------------------------

    private void printException(String msg){
        String[] tokens=msg.split("/");
        tokens[0]=tokens[0].split(":")[1];
        for(String t : tokens){
            System.out.println(t);
        }
    }

    /**
     * Print all the users
     */
    private void showUser(){
        ArrayList<User> users = userService.getAll();
        for(User user : users){
            System.out.println("id= " + user.getId() + ", " + user.toString());
        }
    }

    private void showUserUser(int id){
        List<User> users = userService.getAll().stream().filter(x->x.getId().intValue()!=id).collect(Collectors.toList());
        for(User user : users){
            System.out.println("id= " + user.getId() + ", " + user.toString());
        }
    }

    /**
     * Print all the friendships
     */
    private void showFriendships(){
        ArrayList<Friendship> friendships = friendshipService.getAll();
        for(Friendship f : friendships){
            System.out.println(f.toString());
        }
    }

    /**
     * Print the number of users and the users of the most sociable community
     */
    private void longestCommunities(){
        ArrayList<Integer> result= new ArrayList<>(friendshipService.longestCommunity());
        System.out.println("The number of users is: " + result.size());
        System.out.println("Users are: " + result.toString());
    }

    private void showFriendshipsUser(int id){
        Long id2=Long.parseLong(Integer.toString(id));
        for(String x : friendshipService.showFriendships(id2)){
            System.out.println(x);
        }
    }

    private int  showFriendshipsUserWithId(int id){
        Long id2=Long.parseLong(Integer.toString(id));
        List<String> l = friendshipService.showFriendshipsWithId(id2);
        if(l.size()==0){
            return 0;
        }
        else {
            for (String x : l) {
                System.out.println(x);
            }
            return 1;
        }
    }

    private void showFriendshipUserMonth(int id){
        Long id2=Long.parseLong(Integer.toString(id));
        System.out.println("Month: ");
        String month=in.nextLine();
        for(String x : friendshipService.showFriendshipsMonth(id2,month)){
            System.out.println(x);
        }
    }


//---------------------------------------------------------------------------

    /**
     * Add a user
     */
    private void addUser(){
        String firstName,lastName,date,gender,email,password;

        System.out.println("First Name: ");
        firstName = in.nextLine();

        System.out.println("Last Name: ");
        lastName = in.nextLine();

        System.out.println("Email: ");
        email = in.nextLine();

        System.out.println("Password: ");
        password = in.nextLine();

        if(password.equals("")){
            System.out.println("Password must not be null!");
            return;
        }

        System.out.println("Birthday: ");
        date = in.nextLine();

        System.out.println("Which gender, male(M),female(F),none(N): ");
        gender = in.nextLine();

        try {
            userService.addUser(firstName,lastName,date,gender,email,password,null);
            System.out.println(firstName + " saved successfully");
        } catch (ValidationException | RepositoryException e) {
            printException(e.toString());
        }
    }

    /**
     * Delete a User
     */
    private void deleteUser(){
        showUser();
        String id1;

        System.out.println("Insert an id: ");
        id1=in.nextLine();

        try{
            userService.deleteUser(id1);
            System.out.println("User with id= " + id1 + " successfully deleted");
            //friendshipService.deleteMoreFriendships(id1);
        } catch (RepositoryException e) {
            printException(e.toString());
        }
    }

    /**
     * Add a friendship with the ids provided by the user
     */
    private void addFriendship() {
        showUser();
        String id1, id2;
        System.out.println("Insert first id: ");
        id1 = in.nextLine();
        System.out.println("Insert second id: ");
        id2 = in.nextLine();
        try {
            friendshipService.addFriendship(id1, id2);
            System.out.println("Friendship saved successfully");
        } catch (ValidationException | RepositoryException e) {
            printException(e.toString());
        }
    }

    /**
     * Delete a friendship with the ids provided by the user
     */
    private void deleteFriendship(){
        showFriendships();
        System.out.println("Insert id: ");
        String id = in.nextLine();
        try{
            friendshipService.deleteFriendship(id);
            System.out.println("Friendship successfully deleted");
        } catch (RepositoryException | ValidationException e) {
            printException(e.toString());
        }
    }


//----------------------------------------------------------------------


    private void deleteUserUser(int id){
        String id1= Integer.toString(id);
        try{
            userService.deleteUser(id1);
            System.out.println("User with id= " + id1 + " successfully deleted");
        } catch (RepositoryException e) {
            printException(e.toString());
        }
    }

    private void addFriendshipUser(int x) {
        showUser();
        System.out.println("Insert id of the user: ");
        String id = in.nextLine();
        try {
            friendshipService.addFriendship(Integer.toString(x), id);
            System.out.println("Friendship saved successfully");
        } catch (ValidationException | RepositoryException e) {
            printException(e.toString());
        }
    }

    private void deleteFriendshipUser(int x){
        showFriendshipsUser(x);
        System.out.println("Insert id: ");
        String id = in.nextLine();
        try{
            friendshipService.deleteFriendshipUser(Integer.toString(x),id);
            System.out.println("Friendship successfully deleted");
        } catch (RepositoryException | ValidationException e) {
            printException(e.toString());
        }
    }



//-------------------------------------------------------------

    private void AddChat(int id){
        String name;
        ArrayList<Long> participants=new ArrayList<>();
        participants.add(Long.valueOf(Integer.toString(id)));

        System.out.println("Name: ");
        name=in.nextLine();
        int aux = showFriendshipsUserWithId(id);
        if(aux==0){
            System.out.println("you have no friends so the chat can be completed with the curent user: ");
            showUserUser(id);
        }
        String s="";
        do {
            System.out.println("Participants: ");
            s = in.nextLine();
        } while (s.equals(""));
        for(String x : s.split(" ")){
            try {
                participants.add(Long.parseLong(x));
            } catch (NumberFormatException e) {
                System.out.println("You must insert the id.");
                AddChat(id);
                break;
            }
        }

        try {
            chatService.AddChat(name, participants);
            System.out.println("Chat added successfully");
        } catch (RepositoryException | ValidationException | ServiceException e) {
            System.out.println(e);
        }
    }

    private int showChat(int id){
        List<Chat> l = chatService.getAll().stream()
        .filter(c->c.getParticipants().contains(Long.valueOf(Integer.toString(id)))).collect(Collectors.toList());
        if(l.size()==0) {
            System.out.println("There's no chat");
            return 0;
        }else{
            l.forEach(System.out::println);
            return 1;
        }

    }

    private void selectChat(int id){
        String id_chat="";
        int ok=0;
        int x=showChat(id);
        if(x==1) {
            while (ok == 0) {
                System.out.println("Insert the id of the chat: ");
                id_chat = in.nextLine();
                try {
                    Long.parseLong(id_chat);
                    ok = 1;
                } catch (NumberFormatException e) {
                    System.out.println("Id Chat must be a number!");
                }
            }
            try {
                chatService.verify(id, id_chat);
                writeMessage(id, id_chat);
            } catch (ServiceException e) {
                System.out.println(e);
            }
        }
    }

    private int showConversation(String x){
        Long id=Long.parseLong(x);
        List<Message> l = messageService.getAll().stream().filter(m-> m.getId_chat().equals(id)).collect(Collectors.toList());
        if(l.size()==0) {
            System.out.println("There's no messages");
            return 0;
        }
        else{
            l.forEach(m-> System.out.println(m.toString() + " {" + userService.findOne(Long.toString(m.getId_sender())).getFirstName() + "}"));
            return 1;
        }
    }

    private void writeMessage(int id_user,String id){
        while(true) {
            String r;
            if(showConversation(id)==1) {
                while (true) {
                    System.out.println("Do you wanto to replay? y/n");
                    r = in.nextLine();
                    if (r.equals("y") || r.equals("n"))
                        break;
                }
            }
            else{
                r="n";
            }
            if (r.equals("y")) {
                String message;
                do {
                    System.out.println("Message, @r to refresh or @Exit: ");
                    message = in.nextLine();
                    if(message.equals("@r"))
                        showConversation(id);
                }while(message.equals("@r"));
                if(message.equals("@Error"))
                    break;
                System.out.println("Id message to replay: ");
                String replay = in.nextLine();
                try {
                    Long.parseLong(replay);
                    messageService.addMessage(id, message, replay, Integer.toString(id_user));
                }catch(RepositoryException | ValidationException | ServiceException e){
                    System.out.println(e);
                    selectChat(id_user);
                    break;
                }catch(NumberFormatException e){
                    System.out.println("The id of the replay must be a number");
                }
            } else {
                String message;
                do {
                    System.out.println("Message, @r to refresh or @Exit: ");
                    message = in.nextLine();
                    if(message.equals("@r"))
                        showConversation(id);
                }while(message.equals("@r"));
                if(message.equals("@Error"))
                    break;
                try{
                    messageService.addMessage(id, message,Integer.toString(id_user));
                }catch(RepositoryException | ValidationException | ServiceException e){
                    System.out.println(e);
                    selectChat(id_user);
                    break;
                }
            }
            System.out.println("Send another message? y/n");
            String response = in.nextLine();
            if (response.equals("y")) {
                System.out.println("Ok!!");
            } else {
                break;
            }

        }
    }

    private void showUsersChat(){
        showUser();
        System.out.println("Insert the first id: ");
        String id1=in.nextLine();
        System.out.println("Insert the second id: ");
        String id2=in.nextLine();
        List<Chat> list= null;
        try{
            list = chatService.getPairChatId(id1,id2);
        } catch (ServiceException | RepositoryException e) {
            System.out.println(e.toString());
        }
        ArrayList<Long> id_chats = new ArrayList<>();
        if(list==null){
            System.out.println("There are no chats");
        }
        else {
            for (Chat c : list)
                id_chats.add(c.getId());
            List<Message> messages = messageService.getAll().stream().sorted(Comparator.comparing(Entity::getId)).collect(Collectors.toList());
            for(Long id : id_chats)
                messages.stream().filter(x-> x.getId_chat().equals(id))
                .forEach(x-> System.out.println(x.toString() + " {" + userService.findOne(Long.toString(x.getId_sender())).getLastName() + "}"));
        }
    }

    private void showUsersChatUser(int id_user){
        showUser();
        String id1=Integer.toString(id_user);
        System.out.println("Insert the id: ");
        String id2=in.nextLine();
        List<Chat> list= null;
        try{
            list = chatService.getPairChatId(id1,id2);
        } catch (ServiceException | RepositoryException e) {
            System.out.println(e.toString());
        }
        ArrayList<Long> id_chats = new ArrayList<>();
        if(list==null){
            System.out.println("There are no chats");
        }
        else {
            for (Chat c : list)
                id_chats.add(c.getId());
            List<Message> messages = messageService.getAll().stream().sorted(Comparator.comparing(Entity::getId)).collect(Collectors.toList());
            for(Long id : id_chats)
                messages.stream().filter(x-> x.getId_chat().equals(id))
                        .forEach(x-> System.out.println(x.toString() + " {" + userService.findOne(Long.toString(x.getId_sender())).getLastName() + "}"));
        }
    }


//------------------------------------------------------------

    private void addFriendsRequest(int id) {
        showUserUser(id);
        String id2;
        System.out.println("Insert second id: ");
        id2 = in.nextLine();
        try {
            notificationService.add(Integer.toString(id), id2, NotificationTypes.FRIENDREQUEST,null);
            System.out.println("Request sent");
        } catch (ValidationException | RepositoryException | ServiceException e) {
            printException(e.toString());
        }
    }

    private int showFriedRequest(int id){
        List<Notification> list =  notificationService.getAllUser(id);

        if(list.size()==0) {
            System.out.println("There are no news");
            return 0;
        }
        else {
            list.forEach(x -> System.out.println(x.toString()));
            return 1;
        }
    }

    private void respond(int id){
        int r = showFriedRequest(id);
        if(r==1) {
            System.out.println("Insert the id of the notification");
            String id_not = in.nextLine();
            String response = "";
            int ok = 1;
            while (ok == 1) {
                System.out.println("Response: (a/d)");
                response = in.nextLine();
                if (response.equals("a") || response.equals("d"))
                    ok = 0;
            }
            Notification f;
            try {
                f = notificationService.getOne(id_not);
                if (response.equals("a")) {
                    f.setState(State.accepted);
                    try {
                        friendshipService.addFriendship(f.getId_sender().toString(), f.getId_receiver().toString());
                    } catch(RepositoryException e){
                        System.out.println(e);
                        f.setState(State.declined);
                    }
                } else {
                    f.setState(State.declined);
                }
                notificationService.modify(f);

            } catch (ServiceException | NumberFormatException e) {
                System.out.println(e);
            }
        }
    }


//------------------------------------------------------------


    public void show(){
        String request;
        while(true){
            menu();
            request = in.nextLine();
            switch (request) {
                case "1", "login" -> {
                    int x = login();
                    if (x > 0) {
                        showMenuUser(x);
                    }
                    if (x == 0) {
                        showMenuAdmin();
                    }
                    if (x == -1) {
                        System.out.println("Login failed!");
                    }
                }
                case "2", "new user" -> addUser();
                case "0", "exit" -> {
                    System.out.println("Exit!");
                    System.exit(0);
                }
                default -> System.out.println("the operation is invalid");
            }
        }
    }


    /**
     * The show function handles the menu presentation
     */
    public void showMenuUser(int id){
        String request;
        int ok=1;
        while(ok==1) {
            menuUser(id);
            request = in.nextLine();
            switch (request) {
                case "1" -> showMenuNotification(id);
                case "2" -> showMenuChat(id);
                case "3" -> showUserUser(id);
                case "4" -> addFriendsRequest(id);
                case "5" -> deleteFriendshipUser(id);
                case "6" -> showFriendshipsUser(id);
                case "7" -> showFriendshipUserMonth(id);
                case "8" -> showUsersChatUser(id);
                case "9" -> {deleteUserUser(id);ok=0;}
                case "0" -> ok = 0;
                default -> System.out.println("the operation is invalid");
            }
        }
    }

    public void showMenuAdmin(){
        String request;
        int ok=1;
        while(ok==1) {
            menuAdmin();
            request = in.nextLine();
            switch (request) {
                case "1" -> showUser();
                case "2" -> showFriendships();
                case "3" -> System.out.println("The number of communities is: " + friendshipService.numberCommunities());
                case "4" -> longestCommunities();
                case "5" -> addUser();
                case "6" -> deleteUser();
                case "7" -> addFriendship();
                case "8" -> deleteFriendship();
                case "9" -> showUsersChat();
                case "0" -> ok = 0;
                default -> System.out.println("the operation is invalid");
            }
        }
    }

    public void showMenuChat(int id){
        String request;
        int ok=1;
        while (ok==1){
            menuChat();
            request = in.nextLine();
            switch (request){
                case "1"-> showChat(id);
                case "2"-> AddChat(id);
                case "3"-> selectChat(id);
                case "0"-> ok=0;
                default -> System.out.println("the operation is invalid");
            }
        }
    }

    public void showMenuNotification(int id){
        String request;
        int ok=1;
        while (ok==1){
            menuNotification();
            request = in.nextLine();
            switch (request){
                case "1"-> showFriedRequest(id);
                case "2"-> respond(id);
                case "0"-> ok=0;
                default -> System.out.println("the operation is invalid");
            }
        }
    }
}
