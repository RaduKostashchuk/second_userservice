package my.seconduserservice.dto;

public class LoginDto {
    private String login;
    private String password;

    public static LoginDto of(String login, String password) {
        LoginDto loginDto = new LoginDto();
        loginDto.login = login;
        loginDto.password = password;
        return loginDto;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDto{"
                + "login='" + login + '\''
                + ", password='" + password + '\''
                + '}';
    }
}
