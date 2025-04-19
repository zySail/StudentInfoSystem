package com.example.studentinfosystem;

public class Enrollment {
    private int id;
    private String studentId;
    private String courseName;
    private String teacherId;

    public Enrollment(int id, String studentId, String courseName, String teacherId) {
        this.id = id;
        this.studentId = studentId;
        this.courseName = courseName;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
}
