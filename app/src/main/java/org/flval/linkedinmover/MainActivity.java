package org.flval.linkedinmover;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        List<String> perms = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                perms.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!perms.isEmpty()) {
            int MY_PERMISSIONS_ALL = 1;
            int size = perms.size();
            ActivityCompat.requestPermissions(MainActivity.this, perms.toArray(new String[size]), MY_PERMISSIONS_ALL);
        }
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(view -> rename());
    }

    void rename() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures");
        File[] files = dir.listFiles();
        assert files != null;
        int numbertotreat = files.length;
        TextView textView = findViewById(R.id.textView);
        Pattern pattern = Pattern.compile("image_[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{20}_[0-9a-f]{6}.jpg");
        File finaldir = new File(dir + "/LinkedIn");
        if (finaldir.exists()) {
            if (!finaldir.isDirectory()) {
                finaldir.delete();
            }
        } else {
            finaldir.mkdir();
        }
        int i = 0;
        for (File file : files) {
            String filestring = file.getName();
            Matcher matcher = pattern.matcher(filestring);
            if (matcher.matches()) {
                textView.setText("Found " + file.getName());
                File objective = new File(finaldir.getAbsolutePath() + "/" + file.getName());
                while (objective.exists()) {
                    objective = new File(objective.getAbsolutePath().replace(".jpg", "(copy).jpg"));
                }
                file.renameTo(objective);
            }
            i++;
            progressBar.setProgress((i / numbertotreat) * 100);
        }
        textView.setText("Done");
        progressBar.setVisibility(View.INVISIBLE);
    }
}
