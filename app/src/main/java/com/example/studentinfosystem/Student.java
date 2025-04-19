package com.example.studentinfosystem;

public class Student {
    private String id;
    private String name;
    private String gender;
    private String birth;
    private String phone;
    private String college;
    private String password;

    public Student(String id, String name, String gender, String birth, String phone, String college, String password) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
        this.phone = phone;
        this.college = college;
        this.password = password;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirth() { return birth; }
    public void setBirth(String birth) { this.birth = birth; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    @Override
    public String toString() {
        return name + "（学号：" + id + "）";
    }

}
