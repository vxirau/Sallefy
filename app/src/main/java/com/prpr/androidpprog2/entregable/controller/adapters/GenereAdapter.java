package com.prpr.androidpprog2.entregable.controller.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.model.Genre;


import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GenereAdapter extends RecyclerView.Adapter<GenereAdapter.ViewHolder> {

    private static final String TAG = "GenereAdapter";
    private ArrayList<Genre> generes;
    private Context mContext;
    private GenreCallback mCallback;

    private float fractionAnim;
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f, 0.0f);


    public GenereAdapter(Context context, final GenreCallback itemClickCallback, ArrayList<Genre> generes ) {
        this.generes = generes;
        this.mContext = context;
        this.mCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.genere_item, parent, false);
        return new GenereAdapter.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onGenreSelected(generes.get(position));
            }
        });
        String output = generes.get(position).getName().substring(0, 1).toUpperCase() + generes.get(position).getName().substring(1).toLowerCase();
        holder.nomGenere.setText(output);

    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public int getItemCount() {
        return generes != null ? generes.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mLayout;
        TextView nomGenere;
        public void setFractionAnim() {
            fractionAnim = (float) valueAnimator.getAnimatedValue();;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.genere_item_layout);
            nomGenere = (TextView) itemView.findViewById(R.id.genere_name);

            //Canvi de colors de fons
            //valueAnimator.setDuration(2000);
            valueAnimator.setDuration(getRandomNumber(3500, 5500));
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setFractionAnim();
                    nomGenere.setBackgroundColor(ColorUtils.blendARGB(Color.parseColor("#15C872"), Color.parseColor("#2A9D52"), fractionAnim));
                }
            });
            valueAnimator.setRepeatCount(Animation.INFINITE);
            valueAnimator.start();


        }
    }
}
