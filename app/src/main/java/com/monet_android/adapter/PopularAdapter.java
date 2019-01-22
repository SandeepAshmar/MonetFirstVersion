package com.monet_android.adapter;

import android.content.Context;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.monet_android.halper.IOnItemClickListener;
import com.monet_android.R;
import com.monet_android.modle.popularContent.PopularResponse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private Context ctx;
    private IOnItemClickListener iClickListener;
    private ArrayList<PopularResponse> responseArrayList;
    private Date currentTime;
    private Date date1;
    private SimpleDateFormat simpleDateFormat;
    private String apiTime;
    private String sHours = "", sMinutes = "", sSecounds = "";


    public PopularAdapter(Context ctx, ArrayList<PopularResponse> responseArrayList, IOnItemClickListener iClickListener) {
        this.ctx = ctx;
        this.responseArrayList = responseArrayList;
        this.iClickListener = iClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        PopularResponse popularResponse = responseArrayList.get(position);

        holder.vid_name.setText(popularResponse.getC_title().toString());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        apiTime = popularResponse.getCmp_create_date();

        try {
            date1 = simpleDateFormat.parse(apiTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        timeDiff(date1, holder);

        int seconds = Integer.valueOf(popularResponse.getC_length());
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (minutes <= 9) {
            sMinutes = "0" + minutes;
        }else {
            sMinutes = String.valueOf(minutes);
        }

        if (seconds <= 9) {
            sSecounds = "0" + seconds;
        }else{
            sSecounds = String.valueOf(seconds);
        }

        if (hours == 0) {
            holder.vid_time.setText(sMinutes + ":" + sSecounds);
        } else if (hours <= 9) {
            sHours = "0" + hours;
            holder.vid_time.setText(sHours + sMinutes + ":" + sSecounds);
        }

        if (popularResponse.getC_points().equals("0") && popularResponse.getC_points().equals("1")) {
            holder.tv_point.setText(popularResponse.getC_length().toString() + " XP");
        } else {
            holder.tv_point.setText(popularResponse.getC_length().toString() + " XP");
        }

        if (popularResponse.getC_thumb_url() == null && popularResponse.getC_thumb_url().isEmpty()) {
            holder.thumb.setImageDrawable(null);
        } else {
            Glide.with(ctx).load(popularResponse.getC_thumb_url()).into(holder.thumb);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClickListener != null) {
                    iClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return responseArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView vid_time, vid_name, tv_time_ago, tv_point;

        public ViewHolder(View itemView) {
            super(itemView);

            thumb = itemView.findViewById(R.id.img_thumb);
            vid_name = itemView.findViewById(R.id.tv_vid_name);
            vid_time = itemView.findViewById(R.id.tv_vid_time);
            tv_time_ago = itemView.findViewById(R.id.tv_view_left);
            tv_point = itemView.findViewById(R.id.tv_point);
        }
    }

    private void timeDiff(Date endDate, ViewHolder holder) {
        currentTime = Calendar.getInstance().getTime();
        String dateString = simpleDateFormat.format(currentTime);

        try {
            currentTime = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long different = currentTime.getTime() - endDate.getTime();

        long secoundsInMilli = 1000;
        long minutesInMilli = secoundsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthsInMilli = daysInMilli * 30;
        long yearsInMilli = monthsInMilli * 12;

        long years = different / yearsInMilli;
        different = different % yearsInMilli;

        long months = different / monthsInMilli;
        different = different % monthsInMilli;

        long days = different / daysInMilli;
        different = different % daysInMilli;

        long hours = different / hoursInMilli;
        different = different % hoursInMilli;

        long minutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long secounds = different / secoundsInMilli;

        if (years > 0) {
            holder.tv_time_ago.setText(years + " Year ago");
        } else if (months > 0) {
            holder.tv_time_ago.setText(months + " Month ago");
        } else if (days > 0) {
            holder.tv_time_ago.setText(days + " Day ago");
        } else if (hours > 0) {
            holder.tv_time_ago.setText(hours + " Hour ago");
        } else if (minutes > 0) {
            holder.tv_time_ago.setText(minutes + " Minute ago");
        } else if (secounds > 0) {
            holder.tv_time_ago.setText(secounds + " Second ago");
        }

    }

}
