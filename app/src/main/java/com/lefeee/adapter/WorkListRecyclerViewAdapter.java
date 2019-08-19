package com.lefeee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lefeee.bean.WorkList;
import com.lefeee.hxeducation.R;

import java.util.List;

public class WorkListRecyclerViewAdapter extends RecyclerView.Adapter<WorkListRecyclerViewAdapter.ViewHolder> {

    private final List<WorkList> mWorkList;

    public WorkListRecyclerViewAdapter(List<WorkList> worklist) {
        mWorkList = worklist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worklist, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkList worklist = mWorkList.get(position);
        holder.tv_finishtime_end.setText("截止时间:" + worklist.getFinishtime_end());
        holder.tv_finishtime_start.setText("起始时间:" + worklist.getFinishtime_start());
        holder.tv_classId.setText("班级:" + worklist.getClassId());
        holder.tv_tasktype.setText("任务类型:" + worklist.getTasktype());
        holder.tv_paperCode.setText("教材内码:" + worklist.getPaperCode());
        holder.tv_teamaterialversion.setText("教材版本:" + worklist.getTeamaterialversion());
        holder.tv_testpapertitle.setText("试卷标题:" + worklist.getTestpapertitle());
        holder.tv_subjectId.setText("学科:" + worklist.getSubjectId());
    }

    @Override
    public int getItemCount() {
        return mWorkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tv_finishtime_end;
        public final TextView tv_finishtime_start;
        public final TextView tv_classId;
        public final TextView tv_tasktype;
        public final TextView tv_paperCode;
        public final TextView tv_teamaterialversion;
        public final TextView tv_testpapertitle;
        public final TextView tv_subjectId;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_finishtime_end = view.findViewById(R.id.tv_finishtime_end);
            tv_finishtime_start = view.findViewById(R.id.tv_finishtime_start);
            tv_classId = view.findViewById(R.id.tv_classId);
            tv_tasktype = view.findViewById(R.id.tv_tasktype);
            tv_paperCode = view.findViewById(R.id.tv_paperCode);
            tv_teamaterialversion = view.findViewById(R.id.tv_teamaterialversion);
            tv_testpapertitle = view.findViewById(R.id.tv_testpapertitle);
            tv_subjectId = view.findViewById(R.id.tv_subjectId);
        }

    }


}
