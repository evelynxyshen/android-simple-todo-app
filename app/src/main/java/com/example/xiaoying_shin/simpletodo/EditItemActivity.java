package com.example.xiaoying_shin.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    private int currentIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        String currentText = getIntent().getStringExtra("currentText");
        currentIdx = getIntent().getIntExtra("currentIdx", 0);
        EditText etEditItem = (EditText) findViewById(R.id.etEditItem);
        etEditItem.setText(currentText);
    }

    public void onSaveEdit(View v) {
        EditText etEditItem = (EditText) findViewById(R.id.etEditItem);
        Intent data = new Intent();
        data.putExtra("editedItem", etEditItem.getText().toString());
        data.putExtra("editedItemIdx", currentIdx);
        setResult(RESULT_OK, data);
        this.finish();
    }
}
