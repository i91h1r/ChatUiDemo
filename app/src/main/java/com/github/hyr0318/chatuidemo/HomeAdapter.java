package com.github.hyr0318.chatuidemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Description:
 * 作者：hyr on 2016/9/20 14:46
 * 邮箱：2045446584@qq.com
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private Context mContext;

    private List<HomeItem> data;


    public HomeAdapter(List<HomeItem> data, Context context) {

        this.data = data;

        this.mContext =context ;
    }


    @Override public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item, parent,false);
        HomeViewHolder homeViewHolder = new HomeViewHolder(inflate);
        setListener(parent, homeViewHolder, viewType);

        return homeViewHolder;
    }


    private void setListener(ViewGroup parent, final HomeViewHolder homeViewHolder, int viewType) {
        homeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = homeViewHolder.getAdapterPosition();

                    mOnItemClickListener.onItemClick(v, homeViewHolder, data.get(position),
                        position);
                }
            }
        });
    }


    @Override public void onBindViewHolder(HomeViewHolder holder, int position) {

        holder.textView.setText(data.get(position).getName());
    }


    @Override public int getItemCount() {
        return data != null ? data.size() : 0;
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;


        public HomeViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.tv);
        }

    }


    public OnItemClickListener<HomeItem> mOnItemClickListener;


    public interface OnItemClickListener<T> {
        void onItemClick(View view, RecyclerView.ViewHolder holder, T o, int position);

    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
