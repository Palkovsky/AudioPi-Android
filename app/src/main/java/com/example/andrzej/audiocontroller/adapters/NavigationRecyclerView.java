package com.example.andrzej.audiocontroller.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;

import java.util.List;

public class NavigationRecyclerView extends RecyclerView.Adapter<NavigationRecyclerView.NavigationViewHolder>{

    private List<String> dataset;

    private OnItemClickListener itemClickListener;


    public NavigationRecyclerView(List<String> dataset) {
        this.dataset = dataset;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_list_item, null);
        return new NavigationViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        String item = dataset.get(position);
        holder.nameTv.setText(item);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // inner class to hold a reference to each item of RecyclerView
    public class NavigationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout rootLayout;
        public TextView nameTv;

        public NavigationViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rootLayout = (LinearLayout) itemLayoutView.findViewById(R.id.rootLayout);
            nameTv = (TextView) itemLayoutView.findViewById(R.id.nameTv);

            rootLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
