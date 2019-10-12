package com.cbcds.app.unitable.marks;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.cbcds.app.unitable.AppBaseActivity;
import com.cbcds.app.unitable.ContextMenuRecyclerView;
import com.cbcds.app.unitable.R;
import com.cbcds.app.unitable.database.Subject;

import java.util.List;

import static com.cbcds.app.unitable.utils.AppUtils.setThemeFromPreferences;
import static com.cbcds.app.unitable.utils.AppUtils.setupActionBar;

public class MarksActivity extends AppBaseActivity {
    private MarksViewModel model;
    private AverageMarksAdapter mAdapter;
    private LiveData<List<Subject>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeFromPreferences(this);
        setupActionBar(this, R.layout.actionbar);
        setContentView(R.layout.activity_marks);

        model = ViewModelProviders.of(this).get(MarksViewModel.class);
        data = model.getData();

        ContextMenuRecyclerView recyclerView = findViewById(R.id.average_marks_recycler_view);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new AverageMarksAdapter(data.getValue(), this);
        recyclerView.setAdapter(mAdapter);

        final Observer<List<Subject>> subjectObserver = new Observer<List<Subject>>() {
            @Override
            public void onChanged(@Nullable List<Subject> data) {
                mAdapter.setDataset(data);
            }
        };
        data.observe(this, subjectObserver);
    }

    public void onAddSubjectButtonClick(View view) {
        List<String> subjects = model.getAllSubjects();

        final View dlgView = getLayoutInflater().inflate(R.layout.dialog_add_subject, null);
        final AlertDialog dialog = new AlertDialog.Builder(MarksActivity.this).create();
        dialog.setTitle(R.string.string_new_subject);

        final AutoCompleteTextView etSubjectName = dlgView.findViewById(R.id.et_subject);
        etSubjectName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow()
                            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        etSubjectName.requestFocus();
        etSubjectName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects));

        final TextView tvErrorMessage = dlgView.findViewById(R.id.tv_add_subject_error_msg);

        Button okButton = dlgView.findViewById(R.id.dlg_button_ok);
        Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvErrorMessage.setVisibility(View.GONE);

                String subjectName = etSubjectName.getText().toString();
                if (subjectName.trim().length() == 0) {
                    tvErrorMessage.setText(R.string.string_subject_empty_error);
                    tvErrorMessage.setVisibility(View.VISIBLE);
                } else if (subjectName.length() >= 50) {
                    tvErrorMessage.setText(R.string.string_subject_length_error);
                    tvErrorMessage.setVisibility(View.VISIBLE);
                } else if (model.getSubjectByName(subjectName) != null) {
                    tvErrorMessage.setText(R.string.string_subject_exists_error);
                    tvErrorMessage.setVisibility(View.VISIBLE);
                } else {
                    Subject subject = new Subject();
                    subject.setSubject(subjectName);
                    subject.setAverage(-1);
                    model.insertSubject(subject);
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
        getMenuInflater().inflate(R.menu.context_menu_average, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cm_average_delete:
                final ContextMenuRecyclerView.RecyclerContextMenuInfo info =
                        (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
                final String selectedSubject = mAdapter.getItem(info.position).getSubject();

                final View dlgView = getLayoutInflater().inflate(R.layout.dialog_delete, null);
                final AlertDialog dialog = new AlertDialog.Builder(MarksActivity.this).create();
                TextView tvMessage = dlgView.findViewById(R.id.tv_delete_message);
                final String message = getString(R.string.string_delete_message);
                tvMessage.setText(String.format(message, selectedSubject));

                Button deleteButton = dlgView.findViewById(R.id.dlg_button_delete);
                Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        synchronized (data) {
                            model.deleteSubjectById(info.id);
                        }

                        synchronized (data) {
                            Subject subject = mAdapter.getItem(info.position);
                            model.deleteMarksBySubjectName(getApplication(), subject.getSubject());
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
}
