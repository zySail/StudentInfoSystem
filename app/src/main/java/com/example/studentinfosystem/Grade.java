package com.example.studentinfosystem;

public class Grade {
    private int id;
    private String studentId;
    private String score;
    private String courseName;

    public Grade(int id, String studentId, String score, String courseName) {
        this.id = id;
        this.studentId = studentId;
        this.score = score;
        this.courseName = courseName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
}
