package com.said.oubella.so.player.ui.permissions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.said.oubella.so.player.R;
import com.said.oubella.so.player.databinding.ActivityPermissionsBinding;
import com.said.oubella.so.player.ui.home.HomeActivity;

public class PermissionsActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_CODE = 244;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        ActivityPermissionsBinding binding = ActivityPermissionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.requestPermissionsButton.setOnClickListener(view -> requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSIONS_REQUEST_CODE));

        binding.moreButton.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(R.string.more_on_permissions);
            builder.setMessage(R.string.permissions_decription_full);
            builder.setPositiveButton(getString(R.string.alright), (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            int result = grantResults[i];
            if (result == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, permissions[i] + " is denied :( !", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
