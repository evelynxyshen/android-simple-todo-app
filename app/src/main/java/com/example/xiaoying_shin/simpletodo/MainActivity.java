package com.example.xiaoying_shin.simpletodo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView)findViewById(R.id.lvItem);
        readItems();
        itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
    }

    public void onClearAll(View v) {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Clear all todo items")
            .setMessage("Are you sure you want to clear all todo items?")
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    items.clear();
                    itemsAdapter.notifyDataSetChanged();
                    writeItems();
                }
            })
            .setNegativeButton("Cancel", null)
            .create();
        dialog.show();
    }

    private final int REQUEST_CODE = 20;

    public void launchEditView(int itemIndex) {
        String itemText = items.get(itemIndex);
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);

        i.putExtra("currentText", itemText);
        i.putExtra("currentIdx", itemIndex);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String editedItem = data.getExtras().getString("editedItem");
            int editedItemIdx = data.getExtras().getInt("editedItemIdx", 0);
            items.set(editedItemIdx, editedItem);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView,
                                                   View view, int pos, long id) {
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                                      View view, int pos, long id) {
                        String itemText = items.get(pos);
                        launchEditView(pos);
                    }
                }
        );
    }

    private void readItems() {
        File fileDir = getFilesDir();
        File todoFile = new File(fileDir, "todo.txt");
        items = new ArrayList<String>();
        try {
            String fileData = FileUtils.readFileToString(todoFile);
            JSONObject fileJSONObject = new JSONObject(fileData);
            JSONArray jArr = fileJSONObject.getJSONArray("items");
            for (int i = 0; i < jArr.length(); i ++) {
                items.add(jArr.get(i).toString());
            }
        } catch (IOException e) {
            items = new ArrayList<String>();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeItems() {
        File fileDir = getFilesDir();
        File todoFile = new File(fileDir, "todo.txt");
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", "todoItems");
            JSONArray jsonArr = new JSONArray();
            for (int i = 0; i < items.size(); i ++) {
                String currentItemText = items.get(i);
                jsonArr.put(currentItemText);
            }
            obj.put("items", jsonArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.write(todoFile, obj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
