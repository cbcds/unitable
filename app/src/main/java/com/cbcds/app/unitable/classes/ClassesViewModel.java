package com.cbcds.app.unitable.classes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.cbcds.app.unitable.database.Class;
import com.cbcds.app.unitable.database.ClassRepository;
import com.cbcds.app.unitable.database.ClassesTime;
import com.cbcds.app.unitable.database.TimeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassesViewModel extends AndroidViewModel {
    private ClassRepository cRepository;
    private TimeRepository tRepository;
    private LiveData<List<Class>> data;
    private LiveData<List<ClassesTime>> time;
    private int day;

    public ClassesViewModel(Application app, int day) {
        super(app);
        cRepository = new ClassRepository(app, day);
        tRepository = new TimeRepository(app);
        this.day = day;
        data = cRepository.getClasses();
        time = tRepository.getTime();
    }

    public LiveData<List<Class>> getData() {
        return data;
    }

    public LiveData<List<ClassesTime>> getTime() {
        return time;
    }

    public Class getClassByDayAndNumber(int day, int number) {
        return cRepository.getClassByDayAndNumber(day, number);
    }

    public List<String> getAllSubjects() {
        return cRepository.getAllSubjects();
    }

    public void insertClass(Class newClass) {
        cRepository.insertClass(newClass);
    }

    public void deleteClassById(long id) {
        cRepository.deleteClassById(id);
    }

    public void updateClass(Class newClass) {
        cRepository.updateClass(newClass);
    }

    public List<Class> preprocessClasses(List<Class> classes) {
        return cRepository.preprocessData(classes);
    }

    public List<ClassesTime> preprocessTime(List<ClassesTime> classesTime) {
        return tRepository.preprocessData(classesTime);
    }

    public List<Integer> getAvailableNumbers() {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        List<Class> classes = data.getValue();
        if (classes != null) {
            for (int i = 0; i < classes.size(); ++i) {
                Class currClass = classes.get(i);
                if (!currClass.getSubject().isEmpty()) {
                    int index = numbers.indexOf(currClass.getNumber());
                    numbers.remove(index);
                }
            }
        }
        return numbers;
    }
}
