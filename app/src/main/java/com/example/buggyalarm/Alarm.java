package com.example.buggyalarm;

public class Alarm {
    private String id; // ID-ul unic al alarmei
    private int hour;
    private int minute;
    private String melody;

    public Alarm() {
        // Constructorul implicit este necesar pentru Firebase
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


}
