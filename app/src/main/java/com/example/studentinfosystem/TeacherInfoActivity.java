package com.example.studentinfosystem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherInfoActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView teacherInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        dbHelper = new DatabaseHelper(this);
        teacherInfoView = findViewById(R.id.teacherInfoTextView);

        String teacherId = getIntent().getStringExtra("teacherId");

        if (teacherId == null) {
            Toast.makeText(this, "未获取到教师ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTeacherInfo(teacherId);
    }

    private void loadTeacherInfo(String teacherId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Teacher WHERE id=?", new String[]{teacherId});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            String birth = cursor.getString(cursor.getColumnIndexOrThrow("birth"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String college = cursor.getString(cursor.getColumnIndexOrThrow("college"));

            String info = "工号: " + teacherId + "\n" +
                    "姓名: " + name + "\n" +
                    "性别: " + gender + "\n" +
                    "出生年月日: " + birth + "\n" +
                    "手机号: " + phone + "\n" +
                    "学院: " + college;

            teacherInfoView.setText(info);
        } else {
            teacherInfoView.setText("未找到教师信息");
        }

        cursor.close();
        db.close();
    }
}
