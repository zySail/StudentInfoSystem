package com.example.studentinfosystem;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.Nullable;

import java.io.InputStream;
import java.io.OutputStream;

public class AdminActivity extends Activity {

    private static final int IMPORT_REQUEST_CODE = 100;
    private static final int EXPORT_REQUEST_CODE = 200;
    private String currentTable = ""; // 当前操作的表名
    private DatabaseHelper dbHelper;
    private ExcelUtil excelUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);
        excelUtil = new ExcelUtil(this);

        // 为每个导入导出按钮绑定监听器
        initButton(R.id.btnImportStudent, R.id.btnExportStudent, "Student");
        initButton(R.id.btnImportTeacher, R.id.btnExportTeacher, "Teacher");
        initButton(R.id.btnImportCourse, R.id.btnExportCourse, "Course");
        initButton(R.id.btnImportEnrollment, R.id.btnExportEnrollment, "Enrollment");
        initButton(R.id.btnImportGrade, R.id.btnExportGrade, "Grade");
    }

    private void initButton(int importBtnId, int exportBtnId, String tableName) {
        findViewById(importBtnId).setOnClickListener(v -> {
            currentTable = tableName;
            openFileSelectorForImport();
        });

        findViewById(exportBtnId).setOnClickListener(v -> {
            currentTable = tableName;
            openFileSelectorForExport(tableName + ".xlsx");
        });
    }

    // 打开文件选择器以导入
    private void openFileSelectorForImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, IMPORT_REQUEST_CODE);
    }

    // 打开文件创建器以导出
    private void openFileSelectorForExport(String defaultName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, defaultName);
        startActivityForResult(intent, EXPORT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                if (requestCode == IMPORT_REQUEST_CODE) {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    excelUtil.importFromExcel(currentTable, inputStream, db);
                    Toast.makeText(this, currentTable + "导入成功", Toast.LENGTH_SHORT).show();
                } else if (requestCode == EXPORT_REQUEST_CODE) {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    excelUtil.exportToExcel(currentTable, outputStream, db);
                    Toast.makeText(this, currentTable + "导出成功", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "操作失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
