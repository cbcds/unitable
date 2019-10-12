package com.cbcds.app.unitable;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cbcds.app.unitable.classes.ClassesActivity;
import com.cbcds.app.unitable.database.Class;
import com.cbcds.app.unitable.database.ClassRepository;
import com.cbcds.app.unitable.marks.MarksActivity;

import java.util.List;

import static com.cbcds.app.unitable.utils.AppUtils.setThemeFromPreferences;
import static com.cbcds.app.unitable.utils.AppUtils.setupActionBar;
import static com.cbcds.app.unitable.utils.DimensionConverter.dpToPx;
import static com.cbcds.app.unitable.utils.DimensionConverter.pxToDp;

public class MainActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeFromPreferences(this);
        setupActionBar(this, R.layout.actionbar_activity_main);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);
        setupTimetable();
    }

    private void setupTimetable() {
        Application app = getApplication();
        for (int i = 1; i <= 6; ++i) {
            ClassRepository repository = new ClassRepository(app, MainActivity.this, i);
            repository.getClassesAndSetTimetable();
        }
    }

    public void setViews(List<Class> data, int day) {
        ConstraintLayout constraintLayout = findViewById(R.id.layout_timetable);
        if (data != null) {
            int parentViewId = getParentImageViewId(day);
            for (int j = 0; j < data.size(); ++j) {
                String subject = data.get(j).getSubject();
                int completionStatus = data.get(j).getCompletionStatus();
                TextView textView = new TextView(MainActivity.this);
                int tvId = 100 + day * 10 + j;
                textView.setId(tvId);
                textView.setTextColor(Color.WHITE);
                textView.setText(subject);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine(true);
                textView.setElevation(dpToPx(this, 20));
                ConstraintLayout.LayoutParams tvParams = new ConstraintLayout.LayoutParams
                        (dpToPx(this, getResources().getDimension(R.dimen.rectangle_width)),
                                ConstraintLayout.LayoutParams.WRAP_CONTENT);
                tvParams.startToStart = parentViewId;
                if (j != 0) {
                    tvParams.topToBottom = tvId - 1;
                } else {
                    tvParams.topMargin = dpToPx(this, 4);
                    tvParams.topToTop = parentViewId;
                }
                tvParams.setMarginStart(dpToPx(this, 8));
                tvParams.bottomMargin = dpToPx(this, getResources().getDimension(R.dimen.textview_subject_marginBottom));
                textView.setLayoutParams(tvParams);

                ImageView imageView = new ImageView(MainActivity.this);
                int imgId = 200 + day * 10 + j;
                imageView.setId(imgId);
                imageView.setElevation(dpToPx(this, 20));
                int resId = getStatusImageViewId(completionStatus);
                if (resId != -1) {
                    imageView.setImageResource(resId);
                }
                ConstraintLayout.LayoutParams imgParams = new ConstraintLayout.LayoutParams(
                        dpToPx(this, 15), dpToPx(this, 15));
                if (j != 0) {
                    imgParams.endToEnd = imgId - 1;
                } else {
                    imgParams.setMarginEnd(dpToPx(this, 8));
                    imgParams.endToEnd = parentViewId;
                }
                imgParams.topToTop = tvId;
                imgParams.bottomToBottom = tvId;
                imageView.setLayoutParams(imgParams);

                constraintLayout.addView(textView);
                constraintLayout.addView(imageView);
            }
        }
    }

    private int getParentImageViewId(int day) {
        switch (day) {
            case 1:
                return R.id.img_view_monday;
            case 2:
                return R.id.img_view_tuesday;
            case 3:
                return R.id.img_view_wednesday;
            case 4:
                return R.id.img_view_thursday;
            case 5:
                return R.id.img_view_friday;
            case 6:
                return R.id.img_view_saturday;
            default:
                return R.id.img_view_monday;
        }
    }

    private int getStatusImageViewId(int status) {
        switch (status) {
            case 0:
                return -1;
            case 1:
                return R.drawable.ic_green_dot;
            case 2:
                return R.drawable.ic_orange_dot;
            case 3:
                return R.drawable.ic_red_dot;
            default:
                return -1;
        }
    }

    public void onDayViewClick(View v) {
        Intent intent = new Intent(MainActivity.this, ClassesActivity.class);
        int day;
        switch (v.getId()) {
            case R.id.img_view_monday:
                day = 1;
                break;
            case R.id.img_view_tuesday:
                day = 2;
                break;
            case R.id.img_view_wednesday:
                day = 3;
                break;
            case R.id.img_view_thursday:
                day = 4;
                break;
            case R.id.img_view_friday:
                day = 5;
                break;
            case R.id.img_view_saturday:
                day = 6;
                break;
            default:
                day = 1;
        }
        intent.putExtra("NumberOfDay", day);
        startActivity(intent);
    }

    public void onMarksListButtonClick(View v) {
        startActivity(new Intent(MainActivity.this, MarksActivity.class));
    }

    public void onSettingsButtonClick(View view) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }
}
