package com.example.studentinfosystem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class GradeViewActivity extends AppCompatActivity {

    private ListView gradeListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> gradeList;
    private DatabaseHelper dbHelper;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grades);

        studentId = getIntent().getStringExtra("studentId");
        dbHelper = new DatabaseHelper(this);

        gradeListView = findViewById(R.id.gradeListView);
        gradeList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gradeList);
        gradeListView.setAdapter(adapter);

        loadGrades();
    }

    private void loadGrades() {
        gradeList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT g.courseName, t.name AS teacherName, g.score " +
                        "FROM Grade g " +
                        "LEFT JOIN Course c ON g.courseName = c.name " +
                        "LEFT JOIN Teacher t ON c.teacherId = t.id " +
                        "WHERE g.studentId = ?",
                new String[]{studentId}
        );

        while (cursor.moveToNext()) {
            String course = cursor.getString(cursor.getColumnIndexOrThrow("courseName"));
            String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacherName"));
            String score =  cursor.getString(cursor.getColumnIndexOrThrow("score"));

            gradeList.add(course + " - " + (teacher != null ? teacher : "未知教师") + " - 成绩: " + score);
        }

        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }
}
