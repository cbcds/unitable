package com.cbcds.app.unitable.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MarkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMark(Mark mark);

    @Query("SELECT * FROM mark WHERE subject_name = :subjectName ORDER BY marks DESC")
    LiveData<List<Mark>> getAllMarks(String subjectName);

    @Query("DELETE FROM mark WHERE id = :itemId")
    void deleteMarkById(long itemId);

    @Query("DELETE FROM mark WHERE subject_name = :subjectName")
    void deleteMarksBySubjectName(String subjectName);

    @Query("DELETE FROM mark")
    void deleteAllData();
}
