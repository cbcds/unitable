package com.cbcds.app.unitable.editmarks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.cbcds.app.unitable.database.Mark;
import com.cbcds.app.unitable.database.MarkRepository;
import com.cbcds.app.unitable.database.Subject;
import com.cbcds.app.unitable.database.SubjectRepository;

import java.util.List;

public class EditMarksViewModel extends AndroidViewModel {
    private MarkRepository mRepository;
    private SubjectRepository sRepository;
    private LiveData<List<Mark>> data;

    public EditMarksViewModel(Application app, String subjectName) {
        super(app);
        mRepository = new MarkRepository(app, subjectName);
        sRepository = new SubjectRepository(app);
        data = mRepository.getMarks();
    }

    public LiveData<List<Mark>> getData() {
        return data;
    }

    public void insertMark(Mark mark) {
        mRepository.insertMark(mark);
    }

    public void deleteMarkById(long id) {
        mRepository.deleteMarkById(id);
    }

    public Subject getSubjectByName(String subjectName) {
        return sRepository.getSubjectByName(subjectName);
    }

    public void updateSubject(Subject subject) {
        sRepository.updateSubject(subject);
    }
}
