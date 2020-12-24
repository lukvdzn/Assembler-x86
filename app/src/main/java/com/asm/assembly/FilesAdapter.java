package com.asm.assembly;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyViewHolder> {

    private String [] mDataSet;
    private View.OnClickListener onClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public View view;
        public MyViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public FilesAdapter(String[] mDataSet, View.OnClickListener onClickListener)
    {
        this.mDataSet = mDataSet;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //Inflate recycler list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reycler_view_item, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(view);
        //Set clickListener
        CardView cardView = view.findViewById(R.id.card_view_item);
        cardView.setOnClickListener(onClickListener);
        ImageButton imageButton = view.findViewById(R.id.delete_button);
        imageButton.setOnClickListener(onClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        TextView textView = myViewHolder.view.findViewById(R.id.file_name);
        textView.setText(mDataSet[i]);
        ImageButton imageButton = myViewHolder.view.findViewById(R.id.delete_button);
        imageButton.setTag(i);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

}
