package ua.sumy.stpp.web.register.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Mark {
    private int id = -1;

    private int studentId;
    private int groupId;
    private int subjectId;

    private Date setDate;

    private double value;

    public Mark() {

    }

    public Mark(double value) {
        this.value = value;
    }

    public Mark(int studentId, int groupId, int subjectId, Date setDate, int value) {
        this.studentId = studentId;
        this.groupId = groupId;
        this.subjectId = subjectId;
        this.setDate = setDate;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark = (Mark) o;
        return getId() == mark.getId() &&
                getStudentId() == mark.getStudentId() &&
                getSubjectId() == mark.getSubjectId() &&
                getValue() == mark.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStudentId(), getSubjectId(), getValue());
    }

    @Override
    public String toString() {
        Map<Integer, String> stringMarks = new HashMap<>();
        stringMarks.put(2, "плохо");
        stringMarks.put(3, "не оч");
        stringMarks.put(4, "норм");
        stringMarks.put(5, "збс");
        stringMarks.put(7, "зачет"); // зар.
        stringMarks.put(9, "освобожден"); // зв.
        return stringMarks.getOrDefault(value, "вместо оценки расписывали ручку");
    }

    public Date getSetDate() {
        return setDate;
    }

    public void setSetDate(Date setDate) {
        this.setDate = setDate;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
