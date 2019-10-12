package com.cbcds.app.unitable.editmarks;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class EditMarksViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private String mParam;

    public EditMarksViewModelFactory(Application application, String param) {
        mApplication = application;
        mParam = param;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new EditMarksViewModel(mApplication, mParam);
    }
}
