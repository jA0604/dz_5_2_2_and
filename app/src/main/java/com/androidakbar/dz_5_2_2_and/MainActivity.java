package com.androidakbar.dz_5_2_2_and;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "accountDB.txt";
    private static final String SHARE_NAME = "settings";
    private static final String SHARE_KEY_LOCATION = "location";
    private SharedPreferences shpNote;
    private File filePath;

    AccountFileManager accountFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar appToolbar = findViewById(R.id.app_toolbar);
        appToolbar.setTitle(R.string.name_dz);
        appToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryText));
        appToolbar.setTitleTextAppearance(this, R.style.ToolBarTitleTextAppearance);

//        File file = new File(getFilesDir(), FILE_NAME);
//        filePath = getExternalFilesDir(FILE_NAME);

        Button btnRegistration = (Button) findViewById(R.id.btn_registration);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SaveAccount();
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ReadAccount();
            }
        });

        shpNote = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        final CheckBox chkExtend = (CheckBox) findViewById(R.id.chk_extend);
        chkExtend.setChecked(shpNote.getBoolean(SHARE_KEY_LOCATION, false));

        if(chkExtend.isChecked()) {
            //назначение места хранения во внешней памяти
            if (isExternalStorageWritable()) {
                filePath = new File(getExternalFilesDir(null), FILE_NAME);

            } else {
                Toast.makeText(this, "Внешняя память не доступна", Toast.LENGTH_SHORT).show();
            }

        } else {
            filePath = new File(getFilesDir(), FILE_NAME);
        }
        accountFileManager = new AccountFileManager(filePath);



        chkExtend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (chkExtend.isChecked()) {
                    shpNote.edit().putBoolean(SHARE_KEY_LOCATION, true).apply();
                } else {
                    shpNote.edit().putBoolean(SHARE_KEY_LOCATION, false).apply();
                }
            }
        });
    }



    private void ReadAccount() {
        EditText edLogin = (EditText) findViewById(R.id.ed_login);
        EditText edPassword = (EditText) findViewById(R.id.ed_password);
        if (edLogin.getText().length() >= getResources().getInteger(R.integer.min_login) &&
                edPassword.getText().length() >= getResources().getInteger(R.integer.min_password)) {
            //чтение из  файл
            Account account = new Account(edLogin.getText().toString(), edPassword.getText().toString());
            //AccountFileManager accountFileManager = new AccountFileManager(filePath);
            if (accountFileManager.ReadFromFile(this, account)) {
                Toast.makeText(this, "Акаунт авторизован", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Нет такого аккаунта или неверные логин/пароль", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Несоответствие требованиям к длине логина или пароля", Toast.LENGTH_LONG).show();
        }
    }

    private void SaveAccount() {

        EditText edLogin = (EditText) findViewById(R.id.ed_login);
        EditText edPassword = (EditText) findViewById(R.id.ed_password);
        if (edLogin.getText().length() >= getResources().getInteger(R.integer.min_login) &&
                edPassword.getText().length() >= getResources().getInteger(R.integer.min_password)) {
            //записать в файл
            Account account = new Account(edLogin.getText().toString(), edPassword.getText().toString());
            //AccountFileManager accountFileManager = new AccountFileManager(filePath);
            accountFileManager.SaveToFile(this, account);
        } else {
            Toast.makeText(this, "Несоответствие требованиям к длине логина или пароля", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}