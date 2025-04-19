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

public class CourseManagementActivity extends AppCompatActivity {

    private ListView courseListView;
    private Button addCourseButton;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> courseList;
    private DatabaseHelper dbHelper;
    private int selectedCourseId = -1; // 用 ID 替代 courseName

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_management);

        dbHelper = new DatabaseHelper(this);
        courseListView = findViewById(R.id.courseListView);
        addCourseButton = findViewById(R.id.addCourseButton);

        courseList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courseList);
        courseListView.setAdapter(adapter);

        loadCourses();

        addCourseButton.setOnClickListener(v -> showCourseDialog(-1));

        courseListView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = courseList.get(position);
            selectedCourseId = Integer.parseInt(selected.split(" - ")[0]);
            showCourseDialog(selectedCourseId);
        });

        courseListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String selected = courseList.get(position);
            int courseId = Integer.parseInt(selected.split(" - ")[0]);
            deleteCourse(courseId);
            return true;
        });
    }

    private void loadCourses() {
        courseList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Course", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String teacherId = cursor.getString(cursor.getColumnIndexOrThrow("teacherId"));
            courseList.add(id + " - " + name + " - " + teacherId);
        }
        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }

    private void showCourseDialog(int courseId) {
        View view = getLayoutInflater().inflate(R.layout.dialog_course, null);
        EditText editName = view.findViewById(R.id.editCourseName);
        EditText editTeacher = view.findViewById(R.id.editCourseTeacher);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(courseId == -1 ? "添加课程" : "修改课程");

        if (courseId != -1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM Course WHERE id=?", new String[]{String.valueOf(courseId)});
            if (cursor.moveToFirst()) {
                editName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                editTeacher.setText(cursor.getString(cursor.getColumnIndexOrThrow("teacherId")));
            }
            cursor.close();
            db.close();
        }

        builder.setPositiveButton("保存", (dialog, which) -> {
            String name = editName.getText().toString().trim();
            String teacher = editTeacher.getText().toString().trim();
            if (name.isEmpty() || teacher.isEmpty()) {
                Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("teacherId", teacher);

            if (courseId == -1) {
                db.insert("Course", null, values);
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            } else {
                db.update("Course", values, "id=?", new String[]{String.valueOf(courseId)});
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            }

            db.close();
            loadCourses();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void deleteCourse(int courseId) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("是否删除该课程？")
                .setPositiveButton("删除", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("Course", "id=?", new String[]{String.valueOf(courseId)});
                    db.close();
                    loadCourses();
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
