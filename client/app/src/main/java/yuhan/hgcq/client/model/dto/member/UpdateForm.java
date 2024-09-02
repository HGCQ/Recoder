package yuhan.hgcq.client.model.dto.member;

public class UpdateForm {
    private String name;

    private String password;

    public UpdateForm(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UpdateForm{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
