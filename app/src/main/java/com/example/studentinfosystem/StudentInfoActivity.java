package com.example.studentinfosystem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentInfoActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView studentInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        dbHelper = new DatabaseHelper(this);
        studentInfoView = findViewById(R.id.studentInfoView);

        String studentId = getIntent().getStringExtra("studentId");

        if (studentId == null) {
            Toast.makeText(this, "未获取到学生ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadStudentInfo(studentId);
    }

    private void loadStudentInfo(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Student WHERE id=?", new String[]{studentId});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            String birth = cursor.getString(cursor.getColumnIndexOrThrow("birth"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String college = cursor.getString(cursor.getColumnIndexOrThrow("college"));

            String info = "学号: " + studentId + "\n" +
                    "姓名: " + name + "\n" +
                    "性别: " + gender + "\n" +
                    "出生年月日: " + birth + "\n" +
                    "手机号: " + phone + "\n" +
                    "学院: " + college;

            studentInfoView.setText(info);
        } else {
            studentInfoView.setText("未找到学生信息");
        }

        cursor.close();
        db.close();
    }
}
