package com.example.studentinfosystem;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class StudentManagementActivity extends AppCompatActivity {

    private ListView studentListView;
    private Button addStudentButton;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> studentList;
    private ArrayList<String> studentIds;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        studentListView = findViewById(R.id.studentListView);
        addStudentButton = findViewById(R.id.addStudentButton);

        dbHelper = new DatabaseHelper(this);
        studentList = new ArrayList<>();
        studentIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        studentListView.setAdapter(adapter);

        loadStudents();

        addStudentButton.setOnClickListener(v -> showStudentDialog(null));

        studentListView.setOnItemClickListener((parent, view, position, id) -> {
            String studentId = studentIds.get(position);
            showStudentDialog(studentId);
        });

        studentListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String studentId = studentIds.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("删除学生")
                    .setMessage("确定删除该学生吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        deleteStudent(studentId);
                        loadStudents();
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        });
    }

    private void loadStudents() {
        studentList.clear();
        studentIds.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Student", null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            studentIds.add(id);
            studentList.add("学号：" + id + "  姓名：" + name);
        }
        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }

    private void showStudentDialog(String studentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(studentId == null ? "添加学生" : "编辑学生");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_student, null);
        builder.setView(view);

        EditText editId = view.findViewById(R.id.editStudentId);
        EditText editName = view.findViewById(R.id.editStudentName);
        EditText editGender = view.findViewById(R.id.editStudentGender);
        EditText editBirth = view.findViewById(R.id.editStudentBirth);
        EditText editPhone = view.findViewById(R.id.editStudentPhone);
        EditText editCollege = view.findViewById(R.id.editStudentCollege);
        EditText editPassword = view.findViewById(R.id.editStudentPassword);

        if (studentId != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM Student WHERE id=?", new String[]{studentId});
            if (cursor.moveToFirst()) {
                editId.setText(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                editName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                editGender.setText(cursor.getString(cursor.getColumnIndexOrThrow("gender")));
                editBirth.setText(cursor.getString(cursor.getColumnIndexOrThrow("birth")));
                editPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                editCollege.setText(cursor.getString(cursor.getColumnIndexOrThrow("college")));
                editPassword.setText(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                editId.setEnabled(false);
            }
            cursor.close();
            db.close();
        }

        builder.setPositiveButton("保存", (dialog, which) -> {
            String id = editId.getText().toString().trim();
            String name = editName.getText().toString().trim();
            String gender = editGender.getText().toString().trim();
            String birth = editBirth.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String college = editCollege.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (id.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            values.put("gender", gender);
            values.put("birth", birth);
            values.put("phone", phone);
            values.put("college", college);
            values.put("password", password);

            if (studentId == null) {
                db.insert("Student", null, values);
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            } else {
                db.update("Student", values, "id=?", new String[]{studentId});
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            }
            db.close();
            loadStudents();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void deleteStudent(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Student", "id=?", new String[]{id});
        db.close();
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
    }
}
