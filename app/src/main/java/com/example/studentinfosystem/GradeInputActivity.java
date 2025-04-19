package com.example.studentinfosystem;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class GradeInputActivity extends AppCompatActivity {

    ListView courseListView, studentListView;
    ArrayAdapter<Course> courseAdapter;
    ArrayAdapter<Student> studentAdapter;
    ArrayList<Course> courses = new ArrayList<>();
    ArrayList<Student> students = new ArrayList<>();
    DatabaseHelper dbHelper;
    String selectedCourseName = null;  // 修改为课程名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_input);

        courseListView = findViewById(R.id.courseListView);
        studentListView = findViewById(R.id.studentListView);
        dbHelper = new DatabaseHelper(this);

        loadCourses();

        courseListView.setOnItemClickListener((parent, view, position, id) -> {
            Course selectedCourse = courses.get(position);
            selectedCourseName = selectedCourse.getName();  // 获取课程名
            loadStudentsForCourse(selectedCourseName);      // 通过课程名查询学生
        });

        studentListView.setOnItemClickListener((parent, view, position, id) -> {
            Student selectedStudent = students.get(position);
            showEnterGradeDialog(selectedStudent);
        });
    }

    private void loadCourses() {
        courses.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Course", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String teacherId = cursor.getString(cursor.getColumnIndexOrThrow("teacherId"));
            courses.add(new Course(id, name, teacherId));
        }
        cursor.close();
        db.close();

        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courses);
        courseListView.setAdapter(courseAdapter);
    }

    private void loadStudentsForCourse(String courseName) {
        students.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT s.id, s.name, s.gender, s.birth, s.phone, s.college, s.password " +
                "FROM Student s " +
                "JOIN Enrollment e ON s.id = e.studentId " +
                "WHERE e.courseName = ?", new String[]{courseName});  // 使用 courseName 查询
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String gender = cursor.getString(2);
            String birth = cursor.getString(3);
            String phone = cursor.getString(4);
            String college = cursor.getString(5);
            String password = cursor.getString(6);
            students.add(new Student(id, name, gender, birth, phone, college, password));
        }
        cursor.close();
        db.close();

        studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, students);
        studentListView.setAdapter(studentAdapter);
    }

    private void showEnterGradeDialog(Student student) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_grade, null);
        EditText etScore = dialogView.findViewById(R.id.etScore);

        // 查询已有成绩（如果有）
        String existingScore = getGrade(student.getId(), selectedCourseName);
        if (existingScore != null) {
            etScore.setText(existingScore);
        }

        new AlertDialog.Builder(this)
                .setTitle("成绩 - " + student.getName())
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String score = etScore.getText().toString();
                    if (score.isEmpty()) {
                        Toast.makeText(this, "成绩不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveGrade(student.getId(), selectedCourseName, score);
                })
                .setNegativeButton("取消", null)
                .setNeutralButton("删除", (dialog, which) -> {
                    deleteGrade(student.getId(), selectedCourseName);
                })
                .show();
    }

    private String getGrade(String studentId, String courseName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT score FROM Grade WHERE studentId=? AND courseName=?",
                new String[]{studentId, courseName});
        String score = null;
        if (cursor.moveToFirst()) {
            score = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return score;
    }

    private void deleteGrade(String studentId, String courseName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Grade", "studentId=? AND courseName=?", new String[]{studentId, courseName});
        db.close();
        if (rows > 0) {
            Toast.makeText(this, "成绩已删除", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "无成绩可删", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveGrade(String studentId, String courseName, String score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("studentId", studentId);
        cv.put("courseName", courseName);
        cv.put("score", score);  // 保存成绩（字符）

        // 更新已有记录
        int rowsUpdated = db.update("Grade", cv, "studentId=? AND courseName=?",
                new String[]{studentId, courseName});
        if (rowsUpdated == 0) {
            // 如果没有记录，插入新成绩
            db.insert("Grade", null, cv);
        }
        db.close();

        Toast.makeText(this, "成绩录入成功", Toast.LENGTH_SHORT).show();
    }
}
