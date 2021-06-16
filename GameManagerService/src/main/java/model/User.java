package model;

import java.util.Objects;

public class User {

    private final String nickname;
    private final String address;

    public User(String nickname){
        this.nickname = nickname;
        this.address = "";
    }

    public User(String nickname, String address) {
        this.nickname = nickname;
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
