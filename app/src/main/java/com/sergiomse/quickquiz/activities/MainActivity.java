package com.sergiomse.quickquiz.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sergiomse.quickquiz.Q2Application;
import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.adapters.FolderAdapter;
import com.sergiomse.quickquiz.filechooser.FileChooserActivity;

import java.io.File;


public class MainActivity extends AppCompatActivity implements FolderAdapter.OnFolderClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private File rootFolder;
    private File folder;
    private RecyclerView foldersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quick Quiz");

        foldersRecyclerView = (RecyclerView) findViewById(R.id.foldersRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        foldersRecyclerView.setLayoutManager(layoutManager);

        rootFolder = ((Q2Application) getApplication()).getAppRootDir();
        folder = rootFolder;

        FolderAdapter adapter = new FolderAdapter(this, folder, this);
        foldersRecyclerView.setAdapter(adapter);
    }


    private void drawFolders() {
        FolderAdapter adapter = new FolderAdapter(this, folder, this);
        foldersRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if(folder.equals(rootFolder)) {
            finish();
            return;
        }

        folder = folder.getParentFile();
        drawFolders();
    }

    @Override
    public void onFolderClick(FolderAdapter.FolderItem fi) {
        Log.d(TAG, "onFolderClick: " + fi.getFile().getName());

        switch ( fi.getType() ) {
            case FolderAdapter.FolderItem.FOLDER_TYPE_DIRECTORY:
                this.folder = fi.getFile();
                drawFolders();
                break;

            case FolderAdapter.FolderItem.FOLDER_TYPE_PACKAGE:
                Intent intent = new Intent(this, ExerciseActivity.class);
                intent.putExtra("folderPath", fi.getFile().getAbsolutePath());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPlayFolderClick(FolderAdapter.FolderItem folder) {
        Log.d(TAG, "onPlayFolderClick: " + folder.getFile().getName());

        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra("folderPath", folder.getFile().getAbsolutePath());
        startActivity(intent);
    }

    public void addFolder(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_add_folder, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = ((EditText) view.findViewById(R.id.etFolderName)).getText().toString();
                        File newFolder = new File(MainActivity.this.folder, name);
                        if(!newFolder.mkdirs()) {
                            Toast.makeText(MainActivity.this, "No se puede crear el directorio de la aplicación", Toast.LENGTH_LONG).show();
                        }
                        MainActivity.this.drawFolders();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_package:
                Intent intent = new Intent(this, FileChooserActivity.class);
                intent.putExtra("preferredInstallationPath", folder.getAbsolutePath());
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:

        }
    }

}
