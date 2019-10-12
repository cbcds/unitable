package com.cbcds.app.unitable.editmarks;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.cbcds.app.unitable.AppBaseActivity;
import com.cbcds.app.unitable.ContextMenuRecyclerView;
import com.cbcds.app.unitable.R;
import com.cbcds.app.unitable.database.Mark;
import com.cbcds.app.unitable.database.Subject;
import com.cbcds.app.unitable.utils.AppUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.cbcds.app.unitable.utils.AppUtils.setupActionBar;

public class EditMarksActivity extends AppBaseActivity {
    private EditMarksViewModel model;
    private EditMarksAdapter mAdapter;
    private String subjectName;
    private Subject subject;
    private LiveData<List<Mark>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setThemeFromPreferences(this);
        setupActionBar(this, R.layout.actionbar);
        setContentView(R.layout.activity_edit_marks);

        subjectName = getIntent().getExtras().getString("SubjectName");

        model = ViewModelProviders.of(this, new EditMarksViewModelFactory(
                this.getApplication(), subjectName)).get(EditMarksViewModel.class);
        data = model.getData();
        subject = model.getSubjectByName(subjectName);

        ContextMenuRecyclerView recyclerView = findViewById(R.id.edit_marks_recycler_view);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new EditMarksAdapter(data.getValue());
        recyclerView.setAdapter(mAdapter);

        final Observer<List<Mark>> marksObserver = new Observer<List<Mark>>() {
            @Override
            public void onChanged(@Nullable List<Mark> data) {
                mAdapter.setDataset(data);
            }
        };
        data.observe(this, marksObserver);
    }

    public void onAddMarkButtonClick(View v) {
        final View dlgView = getLayoutInflater().inflate(R.layout.dialog_add_mark, null);
        final AlertDialog dialog = new AlertDialog.Builder(EditMarksActivity.this).create();
        dialog.setTitle(R.string.string_new_mark);

        final EditText etMark = dlgView.findViewById(R.id.et_mark);
        etMark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow()
                            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        etMark.requestFocus();

        final DatePicker dpDate = dlgView.findViewById(R.id.dp_mark_date);
        Calendar today = Calendar.getInstance();
        dpDate.init(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    }
                });

        final TextView tvErrorMessage = dlgView.findViewById(R.id.tv_add_mark_error_msg);

        Button okButton = dlgView.findViewById(R.id.dlg_button_ok);
        Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvErrorMessage.setVisibility(View.GONE);

                String markString = etMark.getText().toString();
                int mark;

                try {
                    mark = Integer.parseInt(markString);
                } catch (NumberFormatException e) {
                    tvErrorMessage.setText(R.string.string_mark_range_error);
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    etMark.setText("");
                    return;
                }
                if (mark < 1 || mark > 100) {
                    tvErrorMessage.setText(R.string.string_mark_range_error);
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    etMark.setText("");
                } else {
                    synchronized (data) {
                        subject.setAverage(addAndCalcAverage(mark));
                        model.updateSubject(subject);
                    }

                    synchronized (data) {
                        Mark newMark = new Mark();
                        newMark.setMark(mark);
                        newMark.setDate(new GregorianCalendar(
                                dpDate.getYear(),
                                dpDate.getMonth(),
                                dpDate.getDayOfMonth())
                                .getTime());
                        newMark.setSubject(subjectName);
                        model.insertMark(newMark);
                    }

                    dialog.dismiss();
                }
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
        getMenuInflater().inflate(R.menu.context_menu_edit_marks, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cm_mark_delete:
                final ContextMenuRecyclerView.RecyclerContextMenuInfo info =
                        (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();

                final View dlgView = getLayoutInflater().inflate(R.layout.dialog_delete, null);
                final AlertDialog dialog = new AlertDialog.Builder(EditMarksActivity.this).create();
                TextView tvMessage = dlgView.findViewById(R.id.tv_delete_message);
                tvMessage.setText(R.string.string_delete_mark_message);

                Button deleteButton = dlgView.findViewById(R.id.dlg_button_delete);
                Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        synchronized (data) {
                            int mark = mAdapter.getItem(info.position).getMark();
                            subject.setAverage(substrAndCalcAverage(mark));
                            model.updateSubject(subject);
                        }

                        synchronized (data) {
                            model.deleteMarkById(info.id);
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
        return super.onContextItemSelected(item);
    }

    private float addAndCalcAverage(int newMark) {
        List<Mark> marks = data.getValue();
        try {
            int n = marks.size();
            int sum = newMark;

            for (int i = 0; i < n; ++i) {
                sum += marks.get(i).getMark();
            }

            return (float) sum / (n + 1);
        } catch (NullPointerException e) {
            return (float) newMark;
        }
    }

    private float substrAndCalcAverage(int deletedMark) {
        List<Mark> marks = data.getValue();
        try {
            int n = marks.size();
            int sum = -deletedMark;

            for (int i = 0; i < n; ++i) {
                sum += marks.get(i).getMark();
            }

            if (sum == 0 && n == 1) {
                return -1;
            } else return (float) sum / (n - 1);
        } catch (NullPointerException e) {
            return -1;
        }
    }
}
