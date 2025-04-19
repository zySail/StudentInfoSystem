package com.example.studentinfosystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "StudentInfo.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 学生表（学号为主键）
        db.execSQL("CREATE TABLE Student (" +
                "id TEXT PRIMARY KEY," +
                "name TEXT," +
                "gender TEXT," +
                "birth TEXT," +
                "phone TEXT," +
                "college TEXT," +
                "password TEXT)");

        // 老师表（工号为主键）
        db.execSQL("CREATE TABLE Teacher (" +
                "id TEXT PRIMARY KEY," +
                "name TEXT," +
                "gender TEXT," +
                "birth TEXT," +
                "phone TEXT," +
                "college TEXT," +
                "password TEXT)");

        // 课程表（课程名和教师工号）
        db.execSQL("CREATE TABLE Course (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "teacherId TEXT)");

        // 选课表
        db.execSQL("CREATE TABLE Enrollment (" +
                "id INTEGER," +
                "studentId TEXT," +
                "courseName TEXT," +
                "teacherId TEXT)");

        // 成绩表
        db.execSQL("CREATE TABLE Grade (" +
                "id INTEGER," +
                "studentId TEXT," +
                "score TEXT," +
                "courseName TEXT)");
        // 插入测试管理员账号
        db.execSQL("CREATE TABLE Admin (id TEXT PRIMARY KEY, password TEXT)");
        db.execSQL("INSERT INTO Admin (id, password) VALUES ('admin', 'admin123')");

        // 插入测试老师
        db.execSQL("INSERT INTO Teacher (id, name, gender, birth, phone, college, password) " +
                "VALUES ('T001', '张老师', '男', '1980-05-01', '13500001111', '计算机学院', '123')");

        // 插入测试学生
        db.execSQL("INSERT INTO Student (id, name, gender, birth, phone, college, password) " +
                "VALUES ('S001', '小明', '男', '2002-09-01', '13888888888', '计算机学院', '123')");

        // 插入测试课程
        db.execSQL("INSERT INTO Course (name, teacherId) VALUES ('高等数学', 'T001')");
        db.execSQL("INSERT INTO Course (name, teacherId) VALUES ('数据结构', 'T001')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Teacher");
        db.execSQL("DROP TABLE IF EXISTS Course");
        db.execSQL("DROP TABLE IF EXISTS Enrollment");
        db.execSQL("DROP TABLE IF EXISTS Grade");
        onCreate(db);
    }
}