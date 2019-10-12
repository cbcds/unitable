package com.cbcds.app.unitable.classestime;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.cbcds.app.unitable.database.ClassesTime;
import com.cbcds.app.unitable.database.TimeRepository;

import java.sql.Time;
import java.util.List;

public class TimeViewModel extends AndroidViewModel {
    private TimeRepository tRepository;
    private LiveData<List<ClassesTime>> data;

    public TimeViewModel(Application app) {
        super(app);
        tRepository = new TimeRepository(app);
        data = tRepository.getTime();
    }

    public LiveData<List<ClassesTime>> getData() {
        return data;
    }

    public void insertTime(ClassesTime time) {
        tRepository.insertClassTime(time);
    }

    public void updateTime(ClassesTime time) {
        tRepository.updateClassTime(time);
    }

    public void deleteClassTimeByNumber(int number) {
        tRepository.deleteClassTimeByNumber(number);
    }

    public List<ClassesTime> preprocessData(List<ClassesTime> data) {
        return tRepository.preprocessData(data);
    }

    public boolean isEmptyTime(ClassesTime time) {
        Time emptyTime = new Time(0, 0, 0);
        if (time.getBeginning().getTime() == emptyTime.getTime() &&
                time.getEnd().getTime() == emptyTime.getTime()) {
            return true;
        }
        return false;
    }
}
