package com.cbcds.app.unitable.classes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cbcds.app.unitable.AppBaseActivity;
import com.cbcds.app.unitable.ContextMenuRecyclerView;
import com.cbcds.app.unitable.ImageAdapter;
import com.cbcds.app.unitable.R;
import com.cbcds.app.unitable.classestime.TimeActivity;
import com.cbcds.app.unitable.database.Class;
import com.cbcds.app.unitable.database.ClassesTime;
import com.cbcds.app.unitable.utils.AppUtils;

import java.util.Arrays;
import java.util.List;

import static com.cbcds.app.unitable.utils.AppUtils.setupActionBar;

public class ClassesActivity extends AppBaseActivity {
    private ClassesViewModel model;
    private ClassesAdapter mAdapter;
    private int day;

    private static int[] imgIconDatabase =
            {R.drawable.ic_neutral_dot, R.drawable.ic_green_dot,
                    R.drawable.ic_orange_dot, R.drawable.ic_red_dot};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setThemeFromPreferences(this);
        setupActionBar(this, R.layout.actionbar_activity_classes);
        setContentView(R.layout.activity_classes);

        day = getIntent().getExtras().getInt("NumberOfDay");

        model = ViewModelProviders.of(this, new ClassesViewModelFactory(
                this.getApplication(), day)).get(ClassesViewModel.class);
        LiveData<List<Class>> data = model.getData();
        LiveData<List<ClassesTime>> timeData = model.getTime();

        ContextMenuRecyclerView recyclerView = findViewById(R.id.classes_recycler_view);
        registerForContextMenu(recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<ClassesTime> time = model.preprocessTime(timeData.getValue());
        List<Class> classes = model.preprocessClasses(data.getValue());
        mAdapter = new ClassesAdapter(classes, time, imgIconDatabase, this);
        recyclerView.setAdapter(mAdapter);

        final Observer<List<Class>> classesObserver = new Observer<List<Class>>() {
            @Override
            public void onChanged(@Nullable List<Class> data) {
                mAdapter.setDataset(model.preprocessClasses(data));
            }
        };
        data.observe(this, classesObserver);

        final Observer<List<ClassesTime>> timeObserver = new Observer<List<ClassesTime>>() {
            @Override
            public void onChanged(@Nullable List<ClassesTime> data) {
                mAdapter.setTime(model.preprocessTime(data));
            }
        };
        timeData.observe(this, timeObserver);
    }

    public void onAddClassButtonClick(View view) {
        List<String> subjects = model.getAllSubjects();

        if (model.getData().getValue().size() == 6) {
            Toast.makeText(ClassesActivity.this, R.string.string_add_class_error,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final View dlgView = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
        final AlertDialog dialog = new AlertDialog.Builder(ClassesActivity.this).create();
        dialog.setTitle(R.string.string_new_class);

        final EditText etClassroom = dlgView.findViewById(R.id.et_classroom);
        final AutoCompleteTextView etSubjectName = dlgView.findViewById(R.id.et_class_subject);
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

        final Spinner spinner = dlgView.findViewById(R.id.spinner_number);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                ClassesActivity.this,
                android.R.layout.simple_spinner_item,
                model.getAvailableNumbers());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final TextView tvSubjectErrorMessage = dlgView.findViewById(R.id.tv_class_subject_error_msg);
        final TextView tvClassroomErrorMessage = dlgView.findViewById(R.id.tv_classroom_error_msg);

        Button okButton = dlgView.findViewById(R.id.dlg_button_ok);
        Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSubjectErrorMessage.setVisibility(View.GONE);
                tvClassroomErrorMessage.setVisibility(View.GONE);

                String subjectName = etSubjectName.getText().toString();
                String classroom = etClassroom.getText().toString();
                boolean errorFlag = false;

                if (subjectName.trim().length() == 0) {
                    tvSubjectErrorMessage.setText(R.string.string_subject_empty_error);
                    tvSubjectErrorMessage.setVisibility(View.VISIBLE);
                    errorFlag = true;
                } else if (subjectName.length() >= 50) {
                    tvSubjectErrorMessage.setText(R.string.string_subject_length_error);
                    tvSubjectErrorMessage.setVisibility(View.VISIBLE);
                    errorFlag = true;
                }

                if (classroom.trim().length() == 0) {
                    tvClassroomErrorMessage.setText(R.string.string_classroom_empty_error);
                    tvClassroomErrorMessage.setVisibility(View.VISIBLE);
                    etClassroom.setText("");
                    errorFlag = true;
                } else if (classroom.length() >= 5) {
                    tvClassroomErrorMessage.setText(R.string.string_classroom_length_error);
                    tvClassroomErrorMessage.setVisibility(View.VISIBLE);
                    errorFlag = true;
                }

                int number = (int) spinner.getSelectedItem();

                if (!errorFlag) {
                    Class newClass = new Class();
                    newClass.setSubject(subjectName);
                    newClass.setClassroom(classroom);
                    newClass.setNumber(number);
                    newClass.setHomework("");
                    newClass.setDay(day);
                    newClass.setCompletionStatus(0);
                    model.insertClass(newClass);

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
        getMenuInflater().inflate(R.menu.context_menu_classes, menu);

        ContextMenuRecyclerView.RecyclerContextMenuInfo info =
                (ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo;
        String subjectName = mAdapter.getItem(info.position).getSubject();
        if (subjectName.isEmpty()) {
            menu.findItem(R.id.cm_class_delete).setVisible(false);
            menu.findItem(R.id.cm_class_move).setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final ContextMenuRecyclerView.RecyclerContextMenuInfo info =
                (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        final Class selectedClass = mAdapter.getItem(info.position);
        final View dlgView;
        final AlertDialog dialog;
        switch (item.getItemId()) {
            case R.id.cm_class_delete: {
                final String selectedSubject = selectedClass.getSubject();
                dlgView = getLayoutInflater().inflate(R.layout.dialog_delete, null);
                dialog = new AlertDialog.Builder(ClassesActivity.this).create();
                TextView tvMessage = dlgView.findViewById(R.id.tv_delete_message);
                String message = getString(R.string.string_delete_message);
                tvMessage.setText(String.format(message, selectedSubject));

                Button deleteButton = dlgView.findViewById(R.id.dlg_button_delete);
                Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.deleteClassById(info.id);

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
                break;
            }
            case R.id.cm_class_edit: {
                List<String> subjects = model.getAllSubjects();

                dlgView = getLayoutInflater().inflate(R.layout.dialog_edit_class, null);
                dialog = new AlertDialog.Builder(ClassesActivity.this).create();
                dialog.setTitle(R.string.string_edit_class);

                final EditText etClassroom = dlgView.findViewById(R.id.et_classroom);
                etClassroom.setText(selectedClass.getClassroom());
                final AutoCompleteTextView etSubjectName = dlgView.findViewById(R.id.et_class_subject);
                etSubjectName.setText(selectedClass.getSubject());
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

                final TextView tvSubjectErrorMessage = dlgView.findViewById(R.id.tv_class_subject_error_msg);
                final TextView tvClassroomErrorMessage = dlgView.findViewById(R.id.tv_classroom_error_msg);

                Button okButton = dlgView.findViewById(R.id.dlg_button_ok);
                Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvSubjectErrorMessage.setVisibility(View.INVISIBLE);
                        tvClassroomErrorMessage.setVisibility(View.INVISIBLE);

                        String subjectName = etSubjectName.getText().toString();
                        String classroom = etClassroom.getText().toString();
                        boolean errorFlag = false;

                        if (subjectName.trim().length() == 0) {
                            tvSubjectErrorMessage.setText(R.string.string_subject_empty_error);
                            tvSubjectErrorMessage.setVisibility(View.VISIBLE);
                            errorFlag = true;
                        } else if (subjectName.length() >= 50) {
                            tvSubjectErrorMessage.setText(R.string.string_subject_length_error);
                            tvSubjectErrorMessage.setVisibility(View.VISIBLE);
                            errorFlag = true;
                        }

                        if (classroom.trim().length() == 0) {
                            tvClassroomErrorMessage.setText(R.string.string_classroom_empty_error);
                            tvClassroomErrorMessage.setVisibility(View.VISIBLE);
                            errorFlag = true;
                        } else if (classroom.length() >= 5) {
                            tvClassroomErrorMessage.setText(R.string.string_classroom_length_error);
                            tvClassroomErrorMessage.setVisibility(View.VISIBLE);
                            errorFlag = true;
                        }

                        if (!errorFlag) {
                            if (selectedClass.getSubject().isEmpty()) {
                                selectedClass.setSubject(subjectName);
                                selectedClass.setClassroom(classroom);
                                selectedClass.setHomework("");
                                selectedClass.setCompletionStatus(0);
                                model.insertClass(selectedClass);
                            } else {
                                selectedClass.setSubject(subjectName);
                                selectedClass.setClassroom(classroom);
                                selectedClass.setHomework("");
                                selectedClass.setCompletionStatus(0);
                                model.updateClass(selectedClass);
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
                break;
            }
            case R.id.cm_class_move: {
                dlgView = getLayoutInflater().inflate(R.layout.dialog_move_class, null);
                dialog = new AlertDialog.Builder(ClassesActivity.this).create();
                dialog.setTitle(R.string.string_move_class);

                final Spinner spinnerDay = dlgView.findViewById(R.id.spinner_day);
                final ArrayAdapter<CharSequence> adapterDay = ArrayAdapter.createFromResource(
                        ClassesActivity.this,
                        R.array.array_days,
                        android.R.layout.simple_spinner_item);
                adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDay.setAdapter(adapterDay);

                final Spinner spinnerNumber = dlgView.findViewById(R.id.spinner_number);
                final ArrayAdapter<Integer> adapterNumber = new ArrayAdapter<>(
                        ClassesActivity.this,
                        android.R.layout.simple_spinner_item,
                        Arrays.asList(1, 2, 3, 4, 5, 6));
                adapterNumber.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerNumber.setAdapter(adapterNumber);

                final RadioGroup radioGroup = dlgView.findViewById(R.id.rg_move_class);

                Button okButton = dlgView.findViewById(R.id.dlg_button_ok);
                Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int day = spinnerDay.getSelectedItemPosition() + 1;
                        int number = spinnerNumber.getSelectedItemPosition() + 1;

                        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                        switch (checkedRadioButtonId) {
                            case R.id.rb_replace: {
                                Class replaceClass = model.getClassByDayAndNumber(day, number);
                                if (replaceClass != null) {
                                    model.deleteClassById(replaceClass.getId());
                                }

                                Class selectedClass = mAdapter.getItem(info.position);
                                selectedClass.setDay(day);
                                selectedClass.setNumber(number);
                                model.updateClass(selectedClass);
                                break;
                            }
                            case R.id.rb_swap: {
                                Class selectedClass = mAdapter.getItem(info.position);

                                Class swapClass = model.getClassByDayAndNumber(day, number);
                                if (swapClass != null) {
                                    int oldDay = selectedClass.getDay();
                                    int oldNumber = selectedClass.getNumber();
                                    swapClass.setDay(oldDay);
                                    swapClass.setNumber(oldNumber);
                                    model.updateClass(swapClass);
                                }

                                selectedClass.setDay(day);
                                selectedClass.setNumber(number);
                                model.updateClass(selectedClass);
                                break;
                            }
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
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    public void editHomework(final Class selectedClass) {
        final View dlgView = getLayoutInflater().inflate(R.layout.dialog_edit_homework, null);
        final AlertDialog dialog = new AlertDialog.Builder(ClassesActivity.this).create();
        dialog.setTitle(R.string.string_edit_homework);

        final Spinner spinner = dlgView.findViewById(R.id.spinner_completion);
        ImageAdapter adapter = new ImageAdapter(getApplicationContext(),
                imgIconDatabase);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedClass.getCompletionStatus());

        final EditText etHomework = dlgView.findViewById(R.id.et_homework);
        etHomework.setText(selectedClass.getHomework());
        etHomework.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow()
                            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        etHomework.requestFocus();

        Button okButton = dlgView.findViewById(R.id.dlg_button_ok);
        Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedClass.setHomework(etHomework.getText().toString());
                selectedClass.setCompletionStatus(spinner.getSelectedItemPosition());
                model.updateClass(selectedClass);

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

    public void onEditTimeButtonClick(View view) {
        startActivity(new Intent(ClassesActivity.this, TimeActivity.class));
    }
}
