package com.cbcds.app.unitable.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ClassDao {

    @Update
    void updateClass(Class newClass);

    @Insert
    void insertClass(Class newClass);

    @Query("SELECT * FROM class WHERE day_of_week = :day ORDER BY class_number ASC")
    LiveData<List<Class>> getAllClasses(int day);

    @Query("SELECT * FROM class WHERE day_of_week = :day ORDER BY class_number ASC")
    List<Class> getAllClassesList(int day);

    @Query("SELECT subject_name FROM class ORDER BY subject_name ASC")
    List<String> getAllSubjects();

    @Query("SELECT * FROM class WHERE day_of_week = :day AND class_number = :number")
    List<Class> getClassByDayAndNumber(int day, int number);

    @Query("DELETE FROM class WHERE id = :itemId")
    void deleteClassById(long itemId);

    @Query("DELETE FROM class")
    void deleteAllData();
}
