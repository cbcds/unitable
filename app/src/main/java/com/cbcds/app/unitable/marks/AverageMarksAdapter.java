package com.cbcds.app.unitable.marks;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cbcds.app.unitable.R;
import com.cbcds.app.unitable.database.Subject;
import com.cbcds.app.unitable.editmarks.EditMarksActivity;

import java.util.List;

public class AverageMarksAdapter extends
        RecyclerView.Adapter<AverageMarksAdapter.AverageMarksViewHolder> {
    private List<Subject> mDataset;
    private Context mContext;

    public AverageMarksAdapter(List<Subject> dataset, Context context) {
        mDataset = dataset;
        mContext = context;
    }

    @NonNull
    @Override
    public AverageMarksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_average, parent, false);
        return new AverageMarksAdapter.AverageMarksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AverageMarksAdapter.AverageMarksViewHolder viewHolder, int position) {
        viewHolder.subject.setText(getItem(position).getSubject());
        float average = getItem(position).getAverage();
        if (average != -1) {
            String markString = String.format("%.1f", average);
            viewHolder.average.setText(markString);
        } else {
            viewHolder.average.setText(Character.toString((char) 0x2014));
        }
    }

    public Subject getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        }
        return mDataset.size();
    }

    public void setDataset(List<Subject> data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    public class AverageMarksViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public TextView subject;
        public TextView average;

        public AverageMarksViewHolder(View view) {
            super(view);
            subject = view.findViewById(R.id.tv_subject_average);
            average = view.findViewById(R.id.tv_mark_average);
            view.setLongClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String subjectName = getItem(position).getSubject();
            Intent intent = new Intent(mContext, EditMarksActivity.class);
            intent.putExtra("SubjectName", subjectName);
            mContext.startActivity(intent);
        }
    }
}