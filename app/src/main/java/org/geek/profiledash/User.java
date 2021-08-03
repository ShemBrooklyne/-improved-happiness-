package org.geek.profiledash;

public class User {

    public String user_name;
    public String phone_number;
    public String user_about;
    public String user_email;

    public User() {
    }

    public User(String user_name, String phone_number, String user_about, String user_email) {
        this.user_name = user_name;
        this.phone_number = phone_number;
        this.user_about = user_about;
        this.user_email = user_email;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_name='" + user_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", user_about='" + user_about + '\'' +
                ", user_email='" + user_email + '\'' +
                '}';
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_about() {
        return user_about;
    }

    public void setUser_about(String user_about) {
        this.user_about = user_about;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
