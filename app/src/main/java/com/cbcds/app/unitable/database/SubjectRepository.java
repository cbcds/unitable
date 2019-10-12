package com.cbcds.app.unitable.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class SubjectRepository {
    private final SubjectDao subjectDao;
    private LiveData<List<Subject>> subjects;

    public SubjectRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        subjectDao = db.subjectDao();
        try {
            subjects = new getSubjectsAsyncTask(subjectDao).execute().get();
        } catch (Exception e) {
            subjects = null;
        }
    }

    public LiveData<List<Subject>> getSubjects() {
        return subjects;
    }

    public void insertSubject(Subject subject) {
        new insertSubjectAsyncTask(subjectDao).execute(subject);
    }

    public Subject getSubjectByName(String subjectName) {
        try {
            return new getSubjectByNameAsyncTask(subjectDao).execute(subjectName).get();
        } catch (Exception e) {
            return null;
        }

    }

    public void deleteSubjectById(long id) {
        new deleteSubjectByIdAsyncTask(subjectDao).execute(id);
    }

    public void updateSubject(Subject subject) {
        new updateSubjectAsyncTask(subjectDao).execute(subject);
    }

    public void deleteAllData() {
        new deleteAllDataAsyncTask(subjectDao).execute();
    }

    private static class getSubjectsAsyncTask extends
            AsyncTask<Void, Void, LiveData<List<Subject>>> {
        private SubjectDao mSubjectDao;

        getSubjectsAsyncTask(SubjectDao dao) {
            mSubjectDao = dao;
        }

        @Override
        protected LiveData<List<Subject>> doInBackground(Void... voids) {
            return mSubjectDao.getAllSubjects();
        }
    }

    private static class insertSubjectAsyncTask extends AsyncTask<Subject, Void, Void> {
        private SubjectDao mSubjectDao;

        insertSubjectAsyncTask(SubjectDao dao) {
            mSubjectDao = dao;
        }

        @Override
        protected Void doInBackground(Subject... subjects) {
            mSubjectDao.insertSubject(subjects[0]);
            return null;
        }
    }

    private static class getSubjectByNameAsyncTask extends AsyncTask<String, Void, Subject> {
        private SubjectDao mSubjectDao;

        getSubjectByNameAsyncTask(SubjectDao dao) {
            mSubjectDao = dao;
        }

        @Override
        protected Subject doInBackground(String... strings) {
            Subject[] subjects = mSubjectDao.getSubjectByName(strings[0]);
            if (subjects.length != 0) {
                return subjects[0];
            } else return null;
        }
    }

    private static class deleteSubjectByIdAsyncTask extends AsyncTask<Long, Void, Void> {
        private SubjectDao mSubjectDao;

        deleteSubjectByIdAsyncTask(SubjectDao dao) {
            mSubjectDao = dao;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            mSubjectDao.deleteSubjectById(longs[0]);
            return null;
        }
    }

    private static class updateSubjectAsyncTask extends AsyncTask<Subject, Void, Void> {
        private SubjectDao mSubjectDao;

        updateSubjectAsyncTask(SubjectDao dao) {
            mSubjectDao = dao;
        }

        @Override
        protected Void doInBackground(Subject... subjects) {
            mSubjectDao.updateAverage(subjects[0]);
            return null;
        }
    }

    private static class deleteAllDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private SubjectDao mSubjectDao;

        deleteAllDataAsyncTask(SubjectDao dao) {
            mSubjectDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mSubjectDao.deleteAllData();
            return null;
        }
    }
}
