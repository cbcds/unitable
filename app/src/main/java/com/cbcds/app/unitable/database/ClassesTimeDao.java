package com.cbcds.app.unitable.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ClassesTimeDao {

    @Update
    void updateTime(ClassesTime time);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTime(ClassesTime time);

    @Query("SELECT * FROM classestime ORDER BY class_number ASC")
    LiveData<List<ClassesTime>> getAllClassesTime();

    @Query("SELECT * FROM classestime ORDER BY class_number ASC")
    List<ClassesTime> getAllClassesTimeList();

    @Query("DELETE FROM classestime WHERE class_number = :number")
    void deleteTimeByNumber(long number);
}
