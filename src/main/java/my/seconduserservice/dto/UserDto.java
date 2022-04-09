package my.seconduserservice.dto;

import my.seconduserservice.model.Role;

public class UserDto {
    private int id;
    private String login;
    private String name;
    private String surname;
    private Role role;

    public static UserDto of(int id, String login, Role role) {
        UserDto userDto = new UserDto();
        userDto.id = id;
        userDto.login = login;
        userDto.role = role;
        return userDto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
