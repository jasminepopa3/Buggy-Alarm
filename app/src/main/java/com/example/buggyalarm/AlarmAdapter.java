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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        private TextView txtAmPm;
        private TextView txtWeek;
        private Switch switchAlarm;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtAmPm = itemView.findViewById(R.id.txtAmPm);
            txtWeek = itemView.findViewById(R.id.txtWeek);
            switchAlarm = itemView.findViewById(R.id.switch1);

            switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Avoid triggering listener when setting initial state
                    if (buttonView.isPressed()) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Alarm alarm = alarmList.get(position);
                            alarm.setEnabled(isChecked);

                            // Update Firebase with the new state
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                DatabaseReference alarmRef = FirebaseDatabase.getInstance().getReference()
                                        .child("user_alarms").child(currentUser.getUid()).child(alarm.getId());
                                alarmRef.child("enabled").setValue(isChecked);
                            }
                        }
                    }
                }
            });
        }

        public void bind(Alarm alarm) {
            txtTime.setText(String.format(Locale.getDefault(), "%02d:%02d", alarm.getHour(), alarm.getMinute()));
            txtAmPm.setText(alarm.getPeriod().toLowerCase());
            txtWeek.setText(alarm.getRepeatingDays());
            switchAlarm.setChecked(alarm.isEnabled());
        }
    }
}
