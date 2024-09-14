package yuhan.hgcq.client.model.dto.team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeamCreateForm implements Serializable {

    private String name;
    private List<Long> members = new ArrayList<Long>();

    public TeamCreateForm() {
    }

    public TeamCreateForm(String name, List<Long> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "TeamCreateForm{" +
                "name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}
