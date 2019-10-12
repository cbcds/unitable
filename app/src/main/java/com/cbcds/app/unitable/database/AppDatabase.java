package com.cbcds.app.unitable.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.cbcds.app.unitable.utils.DateConverter;
import com.cbcds.app.unitable.utils.TimeConverter;

@Database(entities = {Subject.class, Mark.class, Class.class, ClassesTime.class},
        version = 1, exportSchema = false)
@TypeConverters({DateConverter.class, TimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase db;

    public static AppDatabase getDatabase(final Context context) {
        if (db == null) {
            synchronized (AppDatabase.class) {
                db = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "subjects-database").build();
            }
        }
        return db;
    }

    public abstract SubjectDao subjectDao();

    public abstract MarkDao markDao();

    public abstract ClassDao classDao();

    public abstract ClassesTimeDao classesTimeDao();
}
