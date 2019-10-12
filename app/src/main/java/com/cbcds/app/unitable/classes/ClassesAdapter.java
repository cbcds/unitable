package com.cbcds.app.unitable.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cbcds.app.unitable.R;
import com.cbcds.app.unitable.database.Class;
import com.cbcds.app.unitable.database.ClassesTime;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class ClassesAdapter extends
        RecyclerView.Adapter<ClassesAdapter.ClassesViewHolder> {
    private List<Class> mDataset;
    private List<ClassesTime> mClassesTime;
    private int[] mImgIconDatabase;
    private Context mContext;

    public ClassesAdapter(List<Class> dataset, List<ClassesTime> time,
                          int[] imgIconDatabase, Context context) {
        mDataset = dataset;
        mClassesTime = time;
        mImgIconDatabase = imgIconDatabase;
        mContext = context;
    }

    @NonNull
    @Override
    public ClassesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_class, parent, false);
        return new ClassesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClassesViewHolder viewHolder, int position) {
        Class currClass = getItem(position);

        ClassesTime time = mClassesTime.get(position);
        String timeString;
        if (!isEmptyTime(time)) {
            String pattern = "HH:mm";
            String beginningString = new SimpleDateFormat(pattern).format(time.getBeginning());
            String endString = new SimpleDateFormat(pattern).format(time.getEnd());
            String dash = Character.toString((char) 0x2014);
            timeString = beginningString + "\n" + dash + "\n" + endString;
        } else {
            String dash = Character.toString((char) 0x2014);
            timeString = dash;
        }

        viewHolder.time.setText(timeString);
        viewHolder.subject.setText(currClass.getSubject());
        viewHolder.homework.setText(currClass.getHomework());
        viewHolder.classroom.setText(currClass.getClassroom());
        int completionStatus = currClass.getCompletionStatus();
        if (completionStatus != 0) {
            viewHolder.completion.setVisibility(View.VISIBLE);
            viewHolder.completion.setImageResource(mImgIconDatabase[completionStatus]);
        } else {
            viewHolder.completion.setVisibility(View.INVISIBLE);
        }

    }

    public Class getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).getId();
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        }
        return mDataset.size();
    }

    public void setDataset(List<Class> data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    public void setTime(List<ClassesTime> classesTime) {
        mClassesTime = classesTime;
        notifyDataSetChanged();
    }

    public boolean isEmptyTime(ClassesTime time) {
        Time emptyTime = new Time(0, 0, 0);
        if (time.getBeginning().getTime() == emptyTime.getTime() &&
                time.getEnd().getTime() == emptyTime.getTime()) {
            return true;
        }
        return false;
    }

    public class ClassesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public TextView time;
        public TextView subject;
        public TextView homework;
        public TextView classroom;
        public ImageView completion;

        public ClassesViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.tv_time);
            subject = view.findViewById(R.id.tv_subject);
            homework = view.findViewById(R.id.tv_homework);
            classroom = view.findViewById(R.id.tv_classroom);
            completion = view.findViewById(R.id.img_view_completion);

            view.setLongClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Class selectedClass = mDataset.get(position);
            if (!selectedClass.getSubject().isEmpty()) {
                ((ClassesActivity) mContext).editHomework(selectedClass);
            }
        }
    }
}

