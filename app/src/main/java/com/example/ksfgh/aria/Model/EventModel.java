package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

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
}
