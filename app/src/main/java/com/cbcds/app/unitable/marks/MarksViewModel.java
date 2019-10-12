package com.cbcds.app.unitable.marks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.cbcds.app.unitable.database.ClassRepository;
import com.cbcds.app.unitable.database.MarkRepository;
import com.cbcds.app.unitable.database.Subject;
import com.cbcds.app.unitable.database.SubjectRepository;

import java.util.List;

public class MarksViewModel extends AndroidViewModel {
    private SubjectRepository sRepository;
    private ClassRepository cRepository;
    private LiveData<List<Subject>> data;

    public MarksViewModel(Application app) {
        super(app);
        sRepository = new SubjectRepository(app);
        cRepository = new ClassRepository(app, 0);
        data = sRepository.getSubjects();
    }

    public LiveData<List<Subject>> getData() {
        return data;
    }

    public List<String> getAllSubjects() {
        return cRepository.getAllSubjects();
    }

    public void insertSubject(Subject subject) {
        sRepository.insertSubject(subject);
    }

    public Subject getSubjectByName(String subjectName) {
        return sRepository.getSubjectByName(subjectName);
    }

    public void deleteSubjectById(long id) {
        sRepository.deleteSubjectById(id);
    }

    public void deleteMarksBySubjectName(Application app, String subjectName) {
        new MarkRepository(app, subjectName).deleteMarksBySubjectName(subjectName);
    }
}
