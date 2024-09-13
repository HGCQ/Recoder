package yuhan.hgcq.client.model.dto.member;

import java.io.Serializable;

public class MemberUpdateForm implements Serializable {

    private String name;
    private String password;

    public MemberUpdateForm() {
    }

    public MemberUpdateForm(String name, String password) {
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
        return "MemberUpdateForm{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
