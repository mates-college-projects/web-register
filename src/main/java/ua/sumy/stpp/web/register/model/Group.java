package ua.sumy.stpp.web.register.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group {
    private int id = -1;

    private String code = "";
    private List<Student> students = new ArrayList<>(25);

    public Group() {

    }

    public Group(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return getId() == group.getId() &&
                getCode().equals(group.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCode());
    }

    @Override
    public String toString() {
        return code;
    }
}
