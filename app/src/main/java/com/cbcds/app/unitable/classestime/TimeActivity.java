package com.cbcds.app.unitable.classestime;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cbcds.app.unitable.AppBaseActivity;
import com.cbcds.app.unitable.ContextMenuRecyclerView;
import com.cbcds.app.unitable.R;
import com.cbcds.app.unitable.database.ClassesTime;
import com.cbcds.app.unitable.utils.AppUtils;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static com.cbcds.app.unitable.utils.AppUtils.setupActionBar;

public class TimeActivity extends AppBaseActivity {
    final private int TIME_PICKER_INTERVAL = 5;

    private TimeViewModel model;
    private TimeAdapter mAdapter;
    private LiveData<List<ClassesTime>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setThemeFromPreferences(this);
        setupActionBar(this, R.layout.actionbar);
        setContentView(R.layout.activity_time);

        model = ViewModelProviders.of(this).get(TimeViewModel.class);
        data = model.getData();

        ContextMenuRecyclerView recyclerView = findViewById(R.id.time_recycler_view);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new TimeAdapter(data.getValue(), this);
        recyclerView.setAdapter(mAdapter);

        final Observer<List<ClassesTime>> timeObserver = new Observer<List<ClassesTime>>() {
            @Override
            public void onChanged(@Nullable List<ClassesTime> data) {
                mAdapter.setDataset(model.preprocessData(data));
            }
        };
        data.observe(this, timeObserver);
    }

    public void onClick(final ClassesTime classTime) {
        final View dlgView = getLayoutInflater().inflate(R.layout.dialog_edit_time, null);
        final AlertDialog dialog = new AlertDialog.Builder(TimeActivity.this).create();
        dialog.setTitle(R.string.string_edit_class_time);

        final TimePicker tpBeginning = dlgView.findViewById(R.id.tp_beginning);
        final TimePicker tpEnd = dlgView.findViewById(R.id.tp_end);
        setTimePickerInterval(tpBeginning);
        setTimePickerInterval(tpEnd);
        tpBeginning.setIs24HourView(true);
        tpEnd.setIs24HourView(true);

        int begHour, begMinute, endHour, endMinute;
        if (mAdapter.isEmptyTime(classTime)) {
            begHour = 11;
            begMinute = 0;
            endHour = 11;
            endMinute = 0;
        } else {
            begHour = classTime.getBeginning().getHours();
            begMinute = classTime.getBeginning().getMinutes();
            endHour = classTime.getEnd().getHours();
            endMinute = classTime.getEnd().getMinutes();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            tpBeginning.setHour(begHour);
            tpBeginning.setMinute(begMinute);
            tpEnd.setHour(endHour);
            tpEnd.setMinute(endMinute);
        } else {
            tpBeginning.setCurrentHour(begHour);
            tpBeginning.setCurrentMinute(begMinute);
            tpEnd.setCurrentHour(endHour);
            tpEnd.setCurrentMinute(endMinute);
        }

        Button okButton = dlgView.findViewById(R.id.dlg_button_ok);
        Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEmptyTime = model.isEmptyTime(classTime);
                Time beginningTime;
                Time endTime;
                if (Build.VERSION.SDK_INT >= 23) {
                    beginningTime = new Time(tpBeginning.getHour(),
                            tpBeginning.getMinute() * TIME_PICKER_INTERVAL, 0);
                    endTime = new Time(tpEnd.getHour(), tpEnd.getMinute() * TIME_PICKER_INTERVAL, 0);
                } else {
                    beginningTime = new Time(tpBeginning.getCurrentHour(),
                            tpBeginning.getCurrentMinute() * TIME_PICKER_INTERVAL, 0);
                    endTime = new Time(tpEnd.getCurrentHour(),
                            tpEnd.getCurrentMinute() * TIME_PICKER_INTERVAL, 0);
                }
                classTime.setBeginning(beginningTime);
                classTime.setEnd(endTime);
                if (isEmptyTime) {
                    model.insertTime(classTime);
                } else {
                    model.updateTime(classTime);
                }
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setView(dlgView);
        dialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_time, menu);

        ContextMenuRecyclerView.RecyclerContextMenuInfo info =
                (ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo;
        ClassesTime selectedTime = mAdapter.getItem(info.position);
        if (model.isEmptyTime(selectedTime)) {
            menu.findItem(R.id.cm_time_delete).setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cm_time_delete:
                final ContextMenuRecyclerView.RecyclerContextMenuInfo info =
                        (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
                final int number = data.getValue().get(info.position).getNumber();

                final View dlgView = getLayoutInflater().inflate(R.layout.dialog_delete, null);
                final AlertDialog dialog = new AlertDialog.Builder(TimeActivity.this).create();
                TextView tvMessage = dlgView.findViewById(R.id.tv_delete_message);
                tvMessage.setText(R.string.string_delete_time_message);

                Button deleteButton = dlgView.findViewById(R.id.dlg_button_delete);
                Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.deleteClassTimeByNumber(number);
                        dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setView(dlgView);
                dialog.show();
        }
        return super.onContextItemSelected(item);
    }

    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            NumberPicker minutePicker = timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "minute", "id", "android"));
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
        }
    }
}
