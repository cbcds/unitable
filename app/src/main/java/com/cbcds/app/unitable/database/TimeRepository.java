package com.cbcds.app.unitable.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class TimeRepository {
    private final ClassesTimeDao timeDao;
    private LiveData<List<ClassesTime>> time;

    public TimeRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        timeDao = db.classesTimeDao();
        try {
            time = new TimeRepository.getAllClassesTimeAsyncTask(timeDao).execute().get();
        } catch (Exception e) {
            time = null;
        }
    }

    public LiveData<List<ClassesTime>> getTime() {
        return time;
    }

    public List<ClassesTime> getTimeList() {
        try {
            return new getAllClassesTimeListAsyncTask(timeDao).execute().get();
        } catch (Exception e) {
            return null;
        }
    }

    public void insertClassTime(ClassesTime time) {
        new insertClassTimeAsyncTask(timeDao).execute(time);
    }

    public void updateClassTime(ClassesTime time) {
        new updateClassTimeAsyncTask(timeDao).execute(time);
    }

    public void deleteClassTimeByNumber(int number) {
        new deleteClassTimeByNumberAsyncTask(timeDao).execute(number);
    }

    private static class getAllClassesTimeAsyncTask
            extends AsyncTask<Void, Void, LiveData<List<ClassesTime>>> {
        private ClassesTimeDao mTimeDao;

        getAllClassesTimeAsyncTask(ClassesTimeDao dao) {
            mTimeDao = dao;
        }

        @Override
        protected LiveData<List<ClassesTime>> doInBackground(Void... voids) {
            return mTimeDao.getAllClassesTime();
        }
    }

    private static class getAllClassesTimeListAsyncTask
            extends AsyncTask<Void, Void, List<ClassesTime>> {
        private ClassesTimeDao mTimeDao;

        getAllClassesTimeListAsyncTask(ClassesTimeDao dao) {
            mTimeDao = dao;
        }

        @Override
        protected List<ClassesTime> doInBackground(Void... voids) {
            return mTimeDao.getAllClassesTimeList();
        }
    }

    private static class insertClassTimeAsyncTask extends AsyncTask<ClassesTime, Void, Void> {
        private ClassesTimeDao mTimeDao;

        insertClassTimeAsyncTask(ClassesTimeDao dao) {
            mTimeDao = dao;
        }

        @Override
        protected Void doInBackground(ClassesTime... times) {
            mTimeDao.insertTime(times[0]);
            return null;
        }
    }

    private static class updateClassTimeAsyncTask extends AsyncTask<ClassesTime, Void, Void> {
        private ClassesTimeDao mTimeDao;

        updateClassTimeAsyncTask(ClassesTimeDao dao) {
            mTimeDao = dao;
        }

        @Override
        protected Void doInBackground(ClassesTime... times) {
            mTimeDao.updateTime(times[0]);
            return null;
        }
    }

    private static class deleteClassTimeByNumberAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ClassesTimeDao mTimeDao;

        deleteClassTimeByNumberAsyncTask(ClassesTimeDao dao) {
            mTimeDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... ints) {
            mTimeDao.deleteTimeByNumber(ints[0]);
            return null;
        }
    }

    public List<ClassesTime> preprocessData(List<ClassesTime> classesTimes) {
        List<ClassesTime> data;
        if (classesTimes != null) {
            data = new ArrayList<>(classesTimes);
        } else {
            data = new ArrayList<>();
        }

        int lastIndex = data.size() - 1;
        int maxNumber = (lastIndex != -1) ? data.get(lastIndex).getNumber() : 0;
        for (int i = 1; i <= maxNumber; ++i) {
            int currNum = data.get(i - 1).getNumber();
            if (currNum != i) {
                ClassesTime emptyTime = new ClassesTime();
                emptyTime.setNumber(i);
                emptyTime.setBeginning(new Time(0, 0, 0));
                emptyTime.setEnd(new Time(0, 0, 0));
                data.add(i - 1, emptyTime);
            }
        }
        for (int i = maxNumber + 1; i <= 6; ++i) {
            ClassesTime emptyTime = new ClassesTime();
            emptyTime.setNumber(i);
            emptyTime.setBeginning(new Time(0, 0, 0));
            emptyTime.setEnd(new Time(0, 0, 0));
            data.add(i - 1, emptyTime);
        }
        return data;
    }
}
