package socialnetwork.domain;

import socialnetwork.Crypto.Crypt;

public class Password {
    private String password;

    public Password(String password) {
        this.password=password;
    }

    public String getPassword(){
        return password;
    }
}
