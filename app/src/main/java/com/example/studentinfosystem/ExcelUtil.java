package com.example.studentinfosystem;

import android.content.Context;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.InputStream;
import java.io.OutputStream;

public class ExcelUtil {

    private Context context;

    public ExcelUtil(Context context) {
        this.context = context;
    }

    public void exportToExcel(String tableName, OutputStream outputStream, SQLiteDatabase db) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(tableName);

        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        if (cursor.moveToFirst()) {
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cursor.getColumnName(i));
            }

            int rowIndex = 1;
            do {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(cursor.getString(i));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }

    public void importFromExcel(String tableName, InputStream inputStream, SQLiteDatabase db) throws Exception {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        db.beginTransaction();
        try {
            db.delete(tableName, null, null); // 清空表数据

            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getPhysicalNumberOfCells();
            String[] columns = new String[colCount];
            for (int i = 0; i < colCount; i++) {
                columns[i] = headerRow.getCell(i).getStringCellValue();
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                StringBuilder values = new StringBuilder();
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    String val = (cell == null) ? "" : cell.toString();
                    values.append("'").append(val.replace("'", "''")).append("'");
                    if (j < colCount - 1) values.append(",");
                }

                String sql = "INSERT INTO " + tableName + " (" + String.join(",", columns) + ") VALUES (" + values + ")";
                db.execSQL(sql);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            inputStream.close();
            workbook.close();
        }
    }
}
