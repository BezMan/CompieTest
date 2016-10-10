package compie.test.silve.compietest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by silve on 10-Oct-16.
 */

class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<DataVideo> data = Collections.emptyList();
    DataVideo current;
    int currentPos = 0;

    // create constructor to innitilize context and data sent from MainActivity
    MyAdapter(Context context, List<DataVideo> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vid_item, parent, false);
        return new MyHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        DataVideo current = data.get(position);
        myHolder.textTitle.setText(current.getTitle());
        myHolder.textLink.setText(current.getLink());



        // load image into imageview using glide
        //TODO:
        Glide.with(context).load(current.getThumb())
//                .placeholder(R.drawable.ic_img_error)
//                .error(R.drawable.ic_img_error)
                .into(myHolder.ivThumb);

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }




    private class MyHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textLink;
        ImageView ivThumb;


        // create constructor to get widget reference
        MyHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            ivThumb = (ImageView) itemView.findViewById(R.id.ivThumb);
            textLink = (TextView) itemView.findViewById(R.id.textLink);
        }

    }

}



