package com.cbcds.app.unitable.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MarkRepository {
    private final MarkDao markDao;
    private LiveData<List<Mark>> marks;

    public MarkRepository(Application app, String subjectName) {
        AppDatabase db = AppDatabase.getDatabase(app);
        markDao = db.markDao();
        try {
            marks = new MarkRepository.getMarksAsyncTask(markDao).execute(subjectName).get();
        } catch (Exception e) {
            marks = null;
        }
    }

    public LiveData<List<Mark>> getMarks() {
        return marks;
    }

    public void insertMark(Mark mark) {
        new insertMarkAsyncTask(markDao).execute(mark);
    }

    public void deleteMarkById(long id) {
        new deleteMarkByIdAsyncTask(markDao).execute(id);
    }

    public void deleteMarksBySubjectName(String subjectName) {
        new deleteMarksBySubjectNameAsyncTask(markDao).execute(subjectName);
    }

    public void deleteAllData() {
        new deleteAllDataAsyncTask(markDao).execute();
    }

    private static class getMarksAsyncTask extends
            AsyncTask<String, Void, LiveData<List<Mark>>> {
        private MarkDao mMarkDao;

        getMarksAsyncTask(MarkDao dao) {
            mMarkDao = dao;
        }

        @Override
        protected LiveData<List<Mark>> doInBackground(String... strings) {
            return mMarkDao.getAllMarks(strings[0]);
        }
    }

    private static class insertMarkAsyncTask extends AsyncTask<Mark, Void, Void> {
        private MarkDao mMarkDao;

        insertMarkAsyncTask(MarkDao dao) {
            mMarkDao = dao;
        }

        @Override
        protected Void doInBackground(Mark... marks) {
            mMarkDao.insertMark(marks[0]);
            return null;
        }
    }

    private static class deleteMarkByIdAsyncTask extends AsyncTask<Long, Void, Void> {
        private MarkDao mMarkDao;

        deleteMarkByIdAsyncTask(MarkDao dao) {
            mMarkDao = dao;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            mMarkDao.deleteMarkById(longs[0]);
            return null;
        }
    }

    private static class deleteMarksBySubjectNameAsyncTask extends AsyncTask<String, Void, Void> {
        private MarkDao mMarkDao;

        deleteMarksBySubjectNameAsyncTask(MarkDao dao) {
            mMarkDao = dao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mMarkDao.deleteMarksBySubjectName(strings[0]);
            return null;
        }
    }

    private static class deleteAllDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private MarkDao mMarkDao;

        deleteAllDataAsyncTask(MarkDao dao) {
            mMarkDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mMarkDao.deleteAllData();
            return null;
        }
    }
}
