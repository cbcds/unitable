package com.cbcds.app.unitable.classestime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cbcds.app.unitable.R;
import com.cbcds.app.unitable.database.ClassesTime;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class TimeAdapter extends
        RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {
    private List<ClassesTime> mDataset;
    private Context mContext;

    public TimeAdapter(List<ClassesTime> dataset, Context context) {
        mDataset = dataset;
        mContext = context;
    }

    @NonNull
    @Override
    public TimeAdapter.TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_time, parent, false);
        return new TimeAdapter.TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeAdapter.TimeViewHolder viewHolder, int position) {
        ClassesTime classesTime = getItem(position);
        String numberString = classesTime.getNumber() + ".";
        viewHolder.number.setText(numberString);

        String beginningString;
        String endString;
        if (!isEmptyTime(classesTime)) {
            String pattern = "HH:mm";
            beginningString = new SimpleDateFormat(pattern).format(classesTime.getBeginning());
            endString = new SimpleDateFormat(pattern).format(classesTime.getEnd());
        } else {
            beginningString = "";
            endString = "";
        }

        viewHolder.beginning.setText(beginningString);
        viewHolder.end.setText(endString);
    }

    public ClassesTime getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        }
        return mDataset.size();
    }

    public void setDataset(List<ClassesTime> data) {
        mDataset = data;
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

    public class TimeViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public TextView number;
        public TextView beginning;
        public TextView end;

        public TimeViewHolder(View view) {
            super(view);
            number = view.findViewById(R.id.tv_number);
            beginning = view.findViewById(R.id.tv_beginning);
            end = view.findViewById(R.id.tv_end);
            view.setLongClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ClassesTime classTime = getItem(getAdapterPosition());
            ((TimeActivity) mContext).onClick(classTime);
        }
    }
}
