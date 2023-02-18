package ru.otus.auth.dto;

import java.util.List;

public class RegisterGameRequest {

    private List<String> users;

    public RegisterGameRequest() {
    }

    public RegisterGameRequest(List<String> users) {
        this.users = users;
    }

    public List<String> getUsers() {
        return users;
    }

    public RegisterGameRequest setUsers(List<String> users) {
        this.users = users;
        return this;
    }
}
