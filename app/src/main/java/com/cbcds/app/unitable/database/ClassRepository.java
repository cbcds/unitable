package com.cbcds.app.unitable.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.cbcds.app.unitable.MainActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClassRepository {
    private final ClassDao classDao;
    private LiveData<List<Class>> classes;
    private int day;
    private Context context;

    public ClassRepository(Application app, int day) {
        AppDatabase db = AppDatabase.getDatabase(app);
        classDao = db.classDao();
        this.day = day;
        try {
            classes = new ClassRepository.getClassesAsyncTask(classDao).execute(day).get();
        } catch (Exception e) {
            classes = null;
        }
    }

    public ClassRepository(Application app, Context context, int day) {
        AppDatabase db = AppDatabase.getDatabase(app);
        classDao = db.classDao();
        this.day = day;
        this.context = context;
    }

    public LiveData<List<Class>> getClasses() {
        return classes;
    }

    public void getClassesAndSetTimetable() {
        new getClassesAndSetTimetableAsyncTask(classDao, context, day).execute();
    }

    public List<Class> getAllClassesList() {
        try {
            return new getAllClassesListAsyncTask(classDao).execute(day).get();
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getAllSubjects() {
        try {
            List<String> subjects = new getAllSubjectsAsyncTask(classDao).execute().get();
            Set<String> subjectsSet = new LinkedHashSet<>(subjects);
            subjects.clear();
            subjects.addAll(subjectsSet);
            return subjects;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void insertClass(Class newClass) {
        new insertClassAsyncTask(classDao).execute(newClass);
    }

    public void updateClass(Class newClass) {
        new updateClassAsyncTask(classDao).execute(newClass);
    }

    public Class getClassByDayAndNumber(int day, int number) {
        try {
            return new getClassByDayAndNumberAsyncTask(classDao).execute(day, number).get();
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteClassById(long id) {
        new deleteClassByIdAsyncTask(classDao).execute(id);
    }

    public void deleteAllData() {
        new deleteAllDataAsyncTask(classDao).execute();
    }

    private static class getClassesAsyncTask extends
            AsyncTask<Integer, Void, LiveData<List<Class>>> {
        private ClassDao mClassDao;

        getClassesAsyncTask(ClassDao dao) {
            mClassDao = dao;
        }

        @Override
        protected LiveData<List<Class>> doInBackground(Integer... integers) {
            return mClassDao.getAllClasses(integers[0]);
        }
    }

    private static class getClassesAndSetTimetableAsyncTask extends AsyncTask<Void, Void, List<Class>> {
        private ClassDao mClassDao;
        private Context mContext;
        private int mDay;

        getClassesAndSetTimetableAsyncTask(ClassDao dao, Context context, int day) {
            mClassDao = dao;
            mContext = context;
            mDay = day;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Class> doInBackground(Void... voids) {
            return mClassDao.getAllClassesList(mDay);
        }

        @Override
        protected void onPostExecute(List<Class> classes) {
            ((MainActivity) mContext).setViews(classes, mDay);
        }
    }

    private static class getAllClassesListAsyncTask extends AsyncTask<Integer, Void, List<Class>> {
        private ClassDao mClassDao;

        getAllClassesListAsyncTask(ClassDao dao) {
            mClassDao = dao;
        }

        @Override
        protected List<Class> doInBackground(Integer... ints) {
            return mClassDao.getAllClassesList(ints[0]);
        }
    }

    private static class getAllSubjectsAsyncTask extends AsyncTask<Void, Void, List<String>> {
        private ClassDao mClassDao;

        getAllSubjectsAsyncTask(ClassDao dao) {
            mClassDao = dao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return mClassDao.getAllSubjects();
        }
    }

    private static class insertClassAsyncTask extends AsyncTask<Class, Void, Void> {
        private ClassDao mClassDao;

        insertClassAsyncTask(ClassDao dao) {
            mClassDao = dao;
        }

        @Override
        protected Void doInBackground(Class... classes) {
            mClassDao.insertClass(classes[0]);
            return null;
        }
    }

    private static class deleteClassByIdAsyncTask extends AsyncTask<Long, Void, Void> {
        private ClassDao mClassDao;

        deleteClassByIdAsyncTask(ClassDao dao) {
            mClassDao = dao;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            mClassDao.deleteClassById(longs[0]);
            return null;
        }
    }

    private static class updateClassAsyncTask extends AsyncTask<Class, Void, Void> {
        private ClassDao mClassDao;

        updateClassAsyncTask(ClassDao dao) {
            mClassDao = dao;
        }

        @Override
        protected Void doInBackground(Class... classes) {
            mClassDao.updateClass(classes[0]);
            return null;
        }
    }

    private static class getClassByDayAndNumberAsyncTask extends AsyncTask<Integer, Void, Class> {
        private ClassDao mClassDao;

        getClassByDayAndNumberAsyncTask(ClassDao dao) {
            mClassDao = dao;
        }

        @Override
        protected Class doInBackground(Integer... integers) {
            int day = integers[0];
            int number = integers[1];
            List<Class> classes = mClassDao.getClassByDayAndNumber(day, number);
            if (classes.size() != 0) {
                return classes.get(0);
            }
            return null;
        }
    }

    private static class deleteAllDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private ClassDao mClassDao;

        deleteAllDataAsyncTask(ClassDao dao) {
            mClassDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mClassDao.deleteAllData();
            return null;
        }
    }

    public List<Class> preprocessData(List<Class> classes) {
        List<Class> data;
        if (classes != null) {
            data = new ArrayList<>(classes);
        } else {
            data = new ArrayList<>();
        }

        if (data.size() != 0) {
            int lastIndex = data.size() - 1;
            int maxNumber = data.get(lastIndex).getNumber();
            for (int i = 1; i <= maxNumber; ++i) {
                int currNum = data.get(i - 1).getNumber();
                if (currNum != i) {
                    Class emptyClass = new Class();
                    emptyClass.setSubject("");
                    emptyClass.setClassroom("");
                    emptyClass.setNumber(i);
                    emptyClass.setHomework("");
                    emptyClass.setDay(day);
                    data.add(i - 1, emptyClass);
                }
            }
        }
        return data;
    }
}
