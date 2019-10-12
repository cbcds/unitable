package com.cbcds.app.unitable.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface SubjectDao {

    @Update
    void updateAverage(Subject subject);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSubject(Subject subject);

    @Query("SELECT * FROM subject WHERE subject_name = :subjectName")
    Subject[] getSubjectByName(String subjectName);

    @Query("DELETE FROM subject WHERE id = :itemId")
    void deleteSubjectById(long itemId);

    @Query("SELECT * FROM subject")
    LiveData<List<Subject>> getAllSubjects();

    @Query("DELETE FROM subject")
    void deleteAllData();
}
