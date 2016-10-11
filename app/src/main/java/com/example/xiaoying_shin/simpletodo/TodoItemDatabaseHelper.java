package com.example.xiaoying_shin.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class TodoItemDatabaseHelper extends SQLiteOpenHelper {
    private static TodoItemDatabaseHelper sInstance;
    private static final String TAG = "TodoItemDBActivity";

    // Database Info
    private static final String DATABASE_NAME = "TodoItemsDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODOITEMS = "TodoItems";
    // TODO: add user related info

    // Post Table Columns
    private static final String KEY_TODOITEM_ID = "id";
    private static final String KEY_TODOITEM_TEXT = "text";
    private static final String KEY_TODOITEM_CREATED_DATE = "created_at";
    private static final String KEY_TODOITEM_DUE_DATE = "due_at";

    public static synchronized TodoItemDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOITEMS_TABLE = "CREATE TABLE " + TABLE_TODOITEMS +
                "(" +
                KEY_TODOITEM_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TODOITEM_TEXT + " TEXT" +
                KEY_TODOITEM_CREATED_DATE + " TEXT" +
                KEY_TODOITEM_DUE_DATE + " TEXT" +
                ")";
        db.execSQL(CREATE_TODOITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOITEMS);
            onCreate(db);
        }
    }

    public void addTodoItem(TodoItem post) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODOITEM_TEXT, post.text);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TODOITEMS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<TodoItem> getAllTodoItems() {
        ArrayList<TodoItem> todoItemList = new ArrayList<TodoItem>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TODOITEMS + " limit 10", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    TodoItem newTodoItem = new TodoItem();
                    newTodoItem.text = cursor.getString(cursor.getColumnIndex(KEY_TODOITEM_TEXT));
                    todoItemList.add(newTodoItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todoItemList;
    }

    public int updateTodoItemText(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODOITEM_TEXT, todoItem.text);

        // Updating profile picture url for user with that userName
        return db.update(TABLE_TODOITEMS, values, KEY_TODOITEM_ID + " = ?",
                new String[] { String.valueOf(todoItem.id) });
    }

    public int updateTodoItemDueDate(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODOITEM_DUE_DATE, todoItem.due_at);

        // Updating profile picture url for user with that userName
        return db.update(TABLE_TODOITEMS, values, KEY_TODOITEM_ID + " = ?",
                new String[] { String.valueOf(todoItem.id) });
    }

    public void deleteAllTodoItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_TODOITEMS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }
}
