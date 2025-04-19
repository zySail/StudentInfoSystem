package com.example.studentinfosystem;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CourseSelectionActivity extends AppCompatActivity {

    private ListView courseListView, enrolledListView;
    private CourseAdapter courseAdapter;
    private ArrayAdapter<String> enrolledAdapter;
    private ArrayList<Course> courseList;
    private ArrayList<String> enrolledCourses;
    private DatabaseHelper dbHelper;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_selection);

        studentId = getIntent().getStringExtra("studentId");
        dbHelper = new DatabaseHelper(this);

        courseListView = findViewById(R.id.courseListView);
        enrolledListView = findViewById(R.id.enrolledListView);

        courseList = new ArrayList<>();
        enrolledCourses = new ArrayList<>();
        courseAdapter = new CourseAdapter();
        enrolledAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, enrolledCourses);

        courseListView.setAdapter(courseAdapter);
        enrolledListView.setAdapter(enrolledAdapter);

        loadCourses();
        loadEnrolledCourses();
    }

    private void loadCourses() {
        courseList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Course", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String teacherId = cursor.getString(cursor.getColumnIndexOrThrow("teacherId"));
            courseList.add(new Course(id, name, teacherId));
        }
        cursor.close();
        db.close();
        courseAdapter.notifyDataSetChanged();
    }

    private void loadEnrolledCourses() {
        enrolledCourses.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT courseName, teacherId FROM Enrollment WHERE studentId=?", new String[]{studentId});
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String teacherId = cursor.getString(1);
            enrolledCourses.add(name + " - " + teacherId);
        }
        cursor.close();
        db.close();
        enrolledAdapter.notifyDataSetChanged();
    }

    private void enrollCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Enrollment WHERE studentId=? AND courseName=?", new String[]{studentId, course.getName()});
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "已选该课程", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();
            values.put("studentId", studentId);
            values.put("courseName", course.getName());
            values.put("teacherId", course.getTeacherId());
            db.insert("Enrollment", null, values);
            Toast.makeText(this, "选课成功", Toast.LENGTH_SHORT).show();
            loadEnrolledCourses();
        }
        cursor.close();
        db.close();
    }

    private void dropCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Enrollment", "studentId=? AND courseName=?", new String[]{studentId, course.getName()});
        db.close();
        Toast.makeText(this, "退选成功", Toast.LENGTH_SHORT).show();
        loadEnrolledCourses();
    }

    // ⬇ 自定义 Adapter
    class CourseAdapter extends BaseAdapter {

        @Override
        public int getCount() { return courseList.size(); }

        @Override
        public Object getItem(int position) { return courseList.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_course, parent, false);
            TextView text = view.findViewById(R.id.textCourseInfo);
            Button btnEnroll = view.findViewById(R.id.btnEnroll);
            Button btnDrop = view.findViewById(R.id.btnDrop);

            Course course = courseList.get(position);
            text.setText(course.getName() + " - " + course.getTeacherId());

            btnEnroll.setOnClickListener(v -> enrollCourse(course));
            btnDrop.setOnClickListener(v -> dropCourse(course));

            return view;
        }
    }
}
