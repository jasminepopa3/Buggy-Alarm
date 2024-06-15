package com.example.buggyalarm;

public class Alarm {
    private String id; // ID-ul unic al alarmei
    private int hour;
    private int minute;
    private String melody;
    private boolean mon;
    private boolean tue;
    private boolean wed;
    private boolean thu;
    private boolean fri;
    private boolean sat;
    private boolean sun;
    private boolean isEnabled;


    public Alarm() {}

    public Alarm(String id, int hour, int minute, String melody, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.melody = melody;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    public Alarm(String id, int hour, int minute, String melody) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.melody = melody;
    }

    // Getters și Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getMelody() {
        return melody;
    }

    public void setMelody(String melody) {
        this.melody = melody;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isTue() {
        return tue;
    }

    public void setTue(boolean tue) {
        this.tue = tue;
    }

    public boolean isWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    public boolean isThu() {
        return thu;
    }

    public void setThu(boolean thu) {
        this.thu = thu;
    }

    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }
    public String getPeriod() {
        if (hour >= 12) {
            return "PM";
        } else {
            return "AM";
        }
    }

    public String getRepeatingDays() {
        StringBuilder builder = new StringBuilder();

        if (mon) {
            builder.append("Mon, ");
        }
        if (tue) {
            builder.append("Tue, ");
        }
        if (wed) {
            builder.append("Wed, ");
        }
        if (thu) {
            builder.append("Thu, ");
        }
        if (fri) {
            builder.append("Fri, ");
        }
        if (sat) {
            builder.append("Sat, ");
        }
        if (sun) {
            builder.append("Sun, ");
        }

        // Remove the last ", " if there are any days appended
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 2);
        }

        return builder.toString();
    }
    public long getTime() {
        long timeInMillis = hour * 3600000 + minute * 60000; // Convert hours and minutes to milliseconds
        return timeInMillis;
    }
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
