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
}
