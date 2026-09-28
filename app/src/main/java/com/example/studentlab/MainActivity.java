package com.example.studentlab;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "student_pref";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TEL = "telephone";

    private EditText edtId, edtName, edtEmail, edtTel;
    private StudentDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtTel = findViewById(R.id.edtTel);

        Button btnSavePref = findViewById(R.id.btnSavePref);
        Button btnLoadPref = findViewById(R.id.btnLoadPref);
        Button btnSaveSQLite = findViewById(R.id.btnSaveSQLite);
        Button btnLoadSQLite = findViewById(R.id.btnLoadSQLite);

        dbHelper = new StudentDbHelper(this);

        btnSavePref.setOnClickListener(v -> save());
        btnLoadPref.setOnClickListener(v -> load());
        btnSaveSQLite.setOnClickListener(v -> saveSQLite());
        btnLoadSQLite.setOnClickListener(v -> loadSQLite());
    }

    // ---------- Lấy / hiển thị dữ liệu trên form ----------

    /** Đọc form thành Student; trả về null nếu dữ liệu không hợp lệ. */
    private Student readForm() {
        String idStr = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String tel = edtTel.getText().toString().trim();

        if (TextUtils.isEmpty(idStr)) {
            edtId.setError("Vui lòng nhập ID");
            return null;
        }
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            edtId.setError("ID phải là số nguyên");
            return null;
        }
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Vui lòng nhập tên");
            return null;
        }
        return new Student(id, name, email, tel);
    }

    private void showStudent(Student s) {
        edtId.setText(String.valueOf(s.getId()));
        edtName.setText(s.getName());
        edtEmail.setText(s.getEmail());
        edtTel.setText(s.getTelephone());
    }

    // ---------- SharedPreferences ----------

    private void save() {
        Student s = readForm();
        if (s == null) return;

        SharedPreferences.Editor editor =
                getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_ID, s.getId());
        editor.putString(KEY_NAME, s.getName());
        editor.putString(KEY_EMAIL, s.getEmail());
        editor.putString(KEY_TEL, s.getTelephone());
        editor.apply();

        Toast.makeText(this, "Đã lưu vào SharedPreferences", Toast.LENGTH_SHORT).show();
    }

    private void load() {
        SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (!pref.contains(KEY_ID)) {
            Toast.makeText(this, "SharedPreferences chưa có dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }
        Student s = new Student(
                pref.getInt(KEY_ID, 0),
                pref.getString(KEY_NAME, ""),
                pref.getString(KEY_EMAIL, ""),
                pref.getString(KEY_TEL, ""));
        showStudent(s);
        Toast.makeText(this, "Đã tải từ SharedPreferences", Toast.LENGTH_SHORT).show();
    }

    // ---------- SQLite ----------

    private void saveSQLite() {
        Student s = readForm();
        if (s == null) return;

        boolean ok = dbHelper.saveStudent(s);
        Toast.makeText(this,
                ok ? "Đã lưu vào SQLite" : "Lưu SQLite thất bại",
                Toast.LENGTH_SHORT).show();
    }

    private void loadSQLite() {
        Student s = dbHelper.getStudent();
        if (s == null) {
            Toast.makeText(this, "SQLite chưa có dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }
        showStudent(s);
        Toast.makeText(this, "Đã tải từ SQLite", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}