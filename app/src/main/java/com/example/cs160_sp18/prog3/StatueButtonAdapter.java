package com.example.cs160_sp18.prog3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatueButtonAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<StatueButton> mStatueButtons;
    private Map<String, Integer> filenameMap = new HashMap<>();

    public StatueButtonAdapter(Context context, List<StatueButton> statueButtons) {
        mContext = context;
        mStatueButtons = statueButtons;
        filenameMap.put("bell_bears", R.drawable.bell_bears);
        filenameMap.put("bench_bears", R.drawable.bench_bears);
        filenameMap.put("les_bears", R.drawable.les_bears);
        filenameMap.put("macchi_bears", R.drawable.macchi_bears);
        filenameMap.put("mlk_bear", R.drawable.mlk_bear);
        filenameMap.put("outside_stadium", R.drawable.outside_stadium);
        filenameMap.put("south_hall", R.drawable.south_hall);
        filenameMap.put("strawberry_creek", R.drawable.strawberry_creek);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_button_layout, parent, false);
        return new StatueButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StatueButton statueButton = mStatueButtons.get(position);
        ((StatueButtonViewHolder) holder).bind(statueButton);
    }

    @Override
    public int getItemCount() {
        return mStatueButtons.size();
    }

    class StatueButtonViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mStatueButtonBubbleLayout;
        public TextView mStatueNameTextView;
        public TextView mDistanceTextView;
        public Button mCommentButton;
        public ImageView mStatueImageView;

        public StatueButtonViewHolder(View itemView) {
            super(itemView);
            mStatueButtonBubbleLayout = itemView.findViewById(R.id.main_button_layout);
            mStatueNameTextView = mStatueButtonBubbleLayout.findViewById(R.id.statue_name_text_view);
            mDistanceTextView = mStatueButtonBubbleLayout.findViewById(R.id.distance_text_view);
            mCommentButton = mStatueButtonBubbleLayout.findViewById(R.id.comment_button);
            mStatueImageView = mStatueButtonBubbleLayout.findViewById(R.id.statue_image_view);
        }

        void bind(StatueButton statueButton) {
            final String name = statueButton.statueName;
            final int distance = statueButton.distance;
            mStatueNameTextView.setText(name);
            mDistanceTextView.setText(distance + " meters away");
            mCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (distance <= 10) {
                        ((MainActivity) mContext).openCommentFeed(name);
                    } else {
                        Toast toast = Toast.makeText(mContext, "Sorry, must be within 10 meters!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
            mStatueImageView.setImageResource(filenameMap.get(statueButton.imageFilename));
        }

    }

}
