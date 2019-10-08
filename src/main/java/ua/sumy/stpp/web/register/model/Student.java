package ua.sumy.stpp.web.register.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Student {
    private int id = -1;

    private String name;
    private Date birthDate;
    private String homeAddress;

    private int groupId;

    private List<Mark> marks;

    public Student() {

    }

    public Student(String name, Date birthDate, String homeAddress, int groupId) {
        this.name = name;
        this.birthDate = birthDate;
        this.homeAddress = homeAddress;
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return getId() == student.getId() &&
                getGroupId() == student.getGroupId() &&
                getName().equals(student.getName()) &&
                getBirthDate().equals(student.getBirthDate()) &&
                getHomeAddress().equals(student.getHomeAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getBirthDate(), getHomeAddress(), getGroupId());
    }
}
