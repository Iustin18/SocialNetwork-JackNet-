package socialnetwork;

import socialnetwork.Crypto.BCrypt;
import socialnetwork.Crypto.Crypt;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;

import socialnetwork.service.*;
import socialnetwork.ui.UserUi;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {

        /*Crypt c = new Crypt();
        System.out.println(c.hashPassword("iustin"));
        System.out.println(c.checkPassword("iustin",c.hashPassword("iustin")));*/

        MainFX.main(args);

    }
}


