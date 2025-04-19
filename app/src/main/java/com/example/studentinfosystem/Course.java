package com.example.studentinfosystem;

public class Course {
    private int id;
    private String name;
    private String teacherId;

    public Course(int id, String name, String teacherId) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    @Override
    public String toString() {
        return name + "（课程ID：" + id + "）";
    }

}
