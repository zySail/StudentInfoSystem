package com.example.studentinfosystem;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private Spinner roleSpinner;
    private Button loginButton;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        roleSpinner = findViewById(R.id.roleSpinner);
        loginButton = findViewById(R.id.loginButton);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"学生", "老师", "管理员"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor;
        if (role.equals("老师")) {
            cursor = db.rawQuery("SELECT * FROM Teacher WHERE id=? AND password=?", new String[]{user, pass});
            if (cursor.moveToFirst()) {
                Toast.makeText(this, "教师登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                intent.putExtra("teacherId", user);
                startActivity(intent);
            } else {
                Toast.makeText(this, "教师账号或密码错误", Toast.LENGTH_SHORT).show();
            }
        } else if(role.equals("学生")){
            cursor = db.rawQuery("SELECT * FROM Student WHERE id=? AND password=?", new String[]{user, pass});
            if (cursor.moveToFirst()) {
                Toast.makeText(this, "学生登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                intent.putExtra("studentId", user);  // 统一字段名
                startActivity(intent);
            } else {
                Toast.makeText(this, "学生账号或密码错误", Toast.LENGTH_SHORT).show();
            }
        }else {
            cursor = db.rawQuery("SELECT * FROM Admin WHERE id=? AND password=?", new String[]{user, pass});
            if (cursor.moveToFirst()) {
                Toast.makeText(this, "管理员登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "管理员账号或密码错误", Toast.LENGTH_SHORT).show();
            }
        }


        if (cursor != null) cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}
