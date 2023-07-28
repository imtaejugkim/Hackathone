package Hackathone.connection;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class User {
    private String id; // _id로 지정
    private String password;

    public User() {}

    public User(String userId, String password) {
        this.id = userId;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
