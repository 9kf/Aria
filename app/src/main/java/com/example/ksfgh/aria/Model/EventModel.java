package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ksfgh on 10/02/2018.
 */

public class EventModel {


    @SerializedName("band_id")
    public int bandId;
    @SerializedName("event_id")
    public int eventId;
    @SerializedName("event_name")
    public String eventName;
    @SerializedName("event_date")
    public String eventDate;
    @SerializedName("event_time")
    public String eventTime;
    @SerializedName("event_venue")
    public String eventVenue;
    @SerializedName("event_location")
    public String eventLocation;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;

    public EventModel(int bandId, int eventId, String eventName, String eventDate, String eventTime, String eventVenue, String eventLocation, String createdAt, String updatedAt) {
        this.bandId = bandId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventVenue = eventVenue;
        this.eventLocation = eventLocation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getFormattedDate(){

        String dateText = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(eventDate);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTime(date);
            dateText = new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateText;
    }

    public String getFormattedTime(){

        String[] choppedTime = eventTime.split(":");
        return String.valueOf(Integer.parseInt(choppedTime[0])%12) + ":" + choppedTime[1] + " " + ((Integer.parseInt(choppedTime[0])>=12) ? "PM" : "AM");
    }
}
