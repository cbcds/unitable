package com.cbcds.app.unitable;

import android.app.Application;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.cbcds.app.unitable.database.Class;
import com.cbcds.app.unitable.database.ClassRepository;
import com.cbcds.app.unitable.database.ClassesTime;
import com.cbcds.app.unitable.database.TimeRepository;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AppRemoteViewsFactory(this.getApplication(), intent);
    }
}

class AppRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<Class> classes;
    private List<ClassesTime> time;
    private Application app;
    private int day;

    public AppRemoteViewsFactory(Application app, Intent intent) {
        this.app = app;
        day = intent.getExtras().getInt("NumberOfDay");
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        ClassRepository cRepository = new ClassRepository(app, day);
        classes = cRepository.preprocessData(cRepository.getAllClassesList());
        time = new TimeRepository(app).getTimeList();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (time != null && classes != null) {
            if (getTime() != null) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Intent fillInIntent = new Intent();

        ClassesTime time = getTime();
        if (time != null) {
            Class nextClass = classes.get(time.getNumber() - 1);
            if (nextClass != null) {
                String pattern = "HH:mm";
                String timeString = new SimpleDateFormat(pattern).format(time.getBeginning());
                String subject = nextClass.getSubject();
                String classroom = nextClass.getClassroom();
                RemoteViews remoteViews = new RemoteViews(app.getPackageName(), R.layout.list_item_widget);
                remoteViews.setTextViewText(R.id.tv_time, timeString);
                remoteViews.setTextViewText(R.id.tv_subject, subject);
                remoteViews.setTextViewText(R.id.tv_classroom, classroom);
                remoteViews.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
                return remoteViews;
            }
        }
        RemoteViews remoteViews = new RemoteViews(app.getPackageName(), R.layout.empty_widget);
        remoteViews.setOnClickFillInIntent(R.id.empty_widget_item, fillInIntent);
        return remoteViews;
    }

    private ClassesTime getTime() {
        Date now = Calendar.getInstance().getTime();
        Time nowTime = new Time(now.getHours(), now.getMinutes(), now.getSeconds());
        for (ClassesTime time : time) {
            Time endTime = time.getEnd();
            if (nowTime.getTime() <= endTime.getTime()) {
                int number = time.getNumber();
                if (number <= classes.size() && !classes.get(number - 1).getSubject().isEmpty()) {
                    return time;
                }
            }
        }
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
