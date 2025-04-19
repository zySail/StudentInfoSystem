package com.example.studentinfosystem;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TeacherActivity extends AppCompatActivity {

    private Button studentInfoButton;
    private Button courseManagementButton;
    private Button btnGradeInput;
    private Button showTeacherInfoButton;  // 新增按钮
    private String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // 获取从登录界面传递过来的教师ID
        teacherId = getIntent().getStringExtra("teacherId");

        studentInfoButton = findViewById(R.id.studentInfoButton);
        courseManagementButton = findViewById(R.id.courseManagementButton);
        btnGradeInput = findViewById(R.id.btnGradeInput);
        showTeacherInfoButton = findViewById(R.id.showTeacherInfoButton);  // 初始化新按钮

        // 学生信息管理按钮点击事件
        studentInfoButton.setOnClickListener(v -> {
            // 跳转到学生信息管理页面
            startActivity(new Intent(TeacherActivity.this, StudentManagementActivity.class));
        });

        // 课程管理按钮点击事件
        courseManagementButton.setOnClickListener(v -> {
            // 跳转到课程管理页面
            startActivity(new Intent(TeacherActivity.this, CourseManagementActivity.class));
        });

        // 成绩录入按钮点击事件
        btnGradeInput.setOnClickListener(v -> {
            // 加载该教师所教授的课程
            ArrayList<String> courseNames = getCoursesForTeacher(teacherId);
            if (courseNames.isEmpty()) {
                Toast.makeText(TeacherActivity.this, "没有课程可供选择", Toast.LENGTH_SHORT).show();
                return;
            }

            // 选择第一个课程进行成绩录入
            String selectedCourse = courseNames.get(0); // 例如，选择第一个课程

            Intent intent = new Intent(TeacherActivity.this, GradeInputActivity.class);
            intent.putExtra("teacherId", teacherId);  // 传递教师ID
            intent.putExtra("courseName", selectedCourse); // 传递课程名
            startActivity(intent);
        });

        // 查看个人信息按钮点击事件
        showTeacherInfoButton.setOnClickListener(v -> {
            // 跳转到个人信息页面
            Intent intent = new Intent(TeacherActivity.this, TeacherInfoActivity.class);
            intent.putExtra("teacherId", teacherId);
            startActivity(intent);
        });
    }

    // 获取该教师所教授的课程
    private ArrayList<String> getCoursesForTeacher(String teacherId) {
        ArrayList<String> courses = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM Course WHERE teacherId=?", new String[]{teacherId});
        while (cursor.moveToNext()) {
            String courseName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            courses.add(courseName);
        }

        cursor.close();
        db.close();
        return courses;
    }
}
