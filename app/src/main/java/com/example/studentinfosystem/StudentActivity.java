package com.example.studentinfosystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StudentActivity extends AppCompatActivity {

    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        studentId = getIntent().getStringExtra("studentId");

        Button btnViewInfo = findViewById(R.id.btnViewInfo);
        Button btnSelectCourse = findViewById(R.id.btnSelectCourse);
        Button btnViewGrades = findViewById(R.id.btnViewGrades);

        btnViewInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentInfoActivity.class);
            intent.putExtra("studentId", studentId);
            startActivity(intent);
        });

        btnSelectCourse.setOnClickListener(v -> {
            Intent intent = new Intent(this, CourseSelectionActivity.class);
            intent.putExtra("studentId", studentId);
            startActivity(intent);
        });

        btnViewGrades.setOnClickListener(v -> {
            Intent intent = new Intent(this, GradeViewActivity.class);
            intent.putExtra("studentId", studentId);
            startActivity(intent);
        });
    }
}
