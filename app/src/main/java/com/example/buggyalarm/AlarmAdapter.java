package com.example.buggyalarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private Context context;
    private List<Alarm> alarmList;
    private OnAlarmLongClickListener longClickListener;

    // Interface for long click listener
    public interface OnAlarmLongClickListener {
        void onLongClick(int position);
    }

    public AlarmAdapter(Context context, List<Alarm> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
    }

    public void setOnAlarmLongClickListener(OnAlarmLongClickListener listener) {
        this.longClickListener = listener;
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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                        longClickListener.onLongClick(position);
                        return true;
                    }
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Alarm alarm = alarmList.get(position);
                        Intent intent = new Intent(context, EditAlarmActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("alarmId", alarm.getId());
                        intent.putExtra("hour", alarm.getHour());
                        intent.putExtra("minute", alarm.getMinute());
                        intent.putExtra("melody", alarm.getMelody());
                        intent.putExtra("bugs", alarm.getBugs());
                        intent.putExtra("language", alarm.getLanguage());
                        intent.putExtra("level", alarm.getLevel());
                        intent.putExtra("mon", alarm.isMon());
                        intent.putExtra("tue", alarm.isTue());
                        intent.putExtra("wed", alarm.isWed());
                        intent.putExtra("thu", alarm.isThu());
                        intent.putExtra("fri", alarm.isFri());
                        intent.putExtra("sat", alarm.isSat());
                        intent.putExtra("sun", alarm.isSun());
                        int requestCode = (int) System.currentTimeMillis();
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                context,
                                requestCode, // Id-ul PendingIntent-ului (trebuie să fie unic pentru fiecare alarmă)
                                intent,
                                PendingIntent.FLAG_IMMUTABLE
                        );
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

            switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Alarm alarm = alarmList.get(position);
                            alarm.setEnabled(isChecked);

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
