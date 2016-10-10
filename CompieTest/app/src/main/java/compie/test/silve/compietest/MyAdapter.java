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
    private List<ItemData> data = Collections.emptyList();

    // create constructor to initialize context and dataList sent from MainActivity
    MyAdapter(Context context, List<ItemData> data) {
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

    // Bind dataList
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind dataList and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        ItemData current = data.get(position);
        myHolder.textTitle.setText(current.getTitle());

        // load image into imageview using glide
        Glide.with(context).load(current.getThumb())
                .placeholder(R.mipmap.ic_launcher)
//                .error(R.mipmap.ic_launcher)
                .into(myHolder.ivThumb);

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }




    private class MyHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        ImageView ivThumb;


        // create constructor to get widget reference
        MyHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            ivThumb = (ImageView) itemView.findViewById(R.id.ivThumb);
        }

    }

}



