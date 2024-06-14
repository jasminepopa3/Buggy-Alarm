package com.example.buggyalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private Context context;
    private List<Alarm> alarmList;

    public AlarmAdapter(Context context, List<Alarm> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.bind(alarm);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTime;
        private TextView txtAmPm; // Define txtAmPm TextView
        private TextView txtWeek;
        private Switch switchAlarm;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtAmPm = itemView.findViewById(R.id.txtAmPm); // Initialize txtAmPm
            txtWeek = itemView.findViewById(R.id.txtWeek);
            switchAlarm = itemView.findViewById(R.id.switch1);
        }

        public void bind(Alarm alarm) {
            // Bind data to views
            txtTime.setText(String.format(Locale.getDefault(), "%02d:%02d", alarm.getHour(), alarm.getMinute()));
            txtAmPm.setText(alarm.getPeriod().toLowerCase()); // Set text for txtAmPm
            txtWeek.setText(alarm.getRepeatingDays());
            switchAlarm.setChecked(alarm.isEnabled());

            // Set click listener for switch or other actions if needed
            switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Handle switch state change if needed
                }
            });
        }
    }
}
