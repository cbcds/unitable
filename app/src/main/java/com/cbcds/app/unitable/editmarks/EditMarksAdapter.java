package com.cbcds.app.unitable.editmarks;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cbcds.app.unitable.R;
import com.cbcds.app.unitable.database.Mark;

import java.text.SimpleDateFormat;
import java.util.List;

public class EditMarksAdapter extends
        RecyclerView.Adapter<EditMarksAdapter.EditMarksViewHolder> {
    private List<Mark> mDataset;

    public EditMarksAdapter(List<Mark> dataset) {
        mDataset = dataset;
    }

    @NonNull
    @Override
    public EditMarksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_marks, parent, false);
        return new EditMarksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditMarksViewHolder viewHolder, int position) {
        viewHolder.mark.setText(Integer.toString(getItem(position).getMark()));
        String pattern = "dd.MM.yy";
        String dateString = new SimpleDateFormat(pattern).format(getItem(position).getDate());
        viewHolder.date.setText(dateString);
    }

    public Mark getItem(int position) {
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

    public void setDataset(List<Mark> data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    public class EditMarksViewHolder extends RecyclerView.ViewHolder {
        public TextView mark;
        public TextView date;

        public EditMarksViewHolder(View view) {
            super(view);
            mark = view.findViewById(R.id.tv_mark);
            date = view.findViewById(R.id.tv_mark_date);
            view.setLongClickable(true);
        }
    }
}
