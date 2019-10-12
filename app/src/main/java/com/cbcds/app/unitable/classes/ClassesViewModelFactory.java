package com.cbcds.app.unitable.classes;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class ClassesViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private int mParam;

    public ClassesViewModelFactory(Application application, int param) {
        mApplication = application;
        mParam = param;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ClassesViewModel(mApplication, mParam);
    }
}
