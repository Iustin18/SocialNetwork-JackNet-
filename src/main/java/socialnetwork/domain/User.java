package socialnetwork.domain;

import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String date;
    private String gender;
    private final Password password;
    private final String email;
    private String image;

    public User(String firstName, String lastName, String date, String gender, String email, String password,String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.gender = gender;
        this.email=email;
        this.password = new Password(password);
        this.image=image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() { return email; }

    public String getPassword(){ return password.getPassword(); }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    //public String getPasswordDecode(){ return password.getPasswordDecode(); }

    @Override
    public String toString() {
        return "firstName= " + firstName +
                ", lastName= " + lastName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getFirstName(), user.getFirstName()) &&
                Objects.equals(getLastName(), user.getLastName()) &&
                Objects.equals(getDate(), user.getDate()) &&
                Objects.equals(getGender(), user.getGender()) &&
                Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getDate(), getGender(), getEmail());
    }
}