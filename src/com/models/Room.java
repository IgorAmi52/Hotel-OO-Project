package com.models;

import java.util.Objects;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.models.enums.RoomStatus;

public class Room {
	private String type;
	private RoomStatus status = RoomStatus.AVAILABLE;
	private final int ID;

	public Room(String type, int ID) {
		this.type = type;
		this.ID = ID;
	}
	public void changeStatus(RoomStatus status) {
		this.status = status;
	}
	public JsonObject getJson() {
		Gson gson = new Gson();
        String jsonString = gson.toJson(Room.this);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        
        return jsonObject;
	}
	public String getType() {
		return type;
	}
	public String getID() {
		return String.valueOf(ID);
	}
	public String getStatus() {
		// TODO Auto-generated method stub
		return this.status.getStatus();
	}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return ID == room.ID &&
                Objects.equals(type, room.type) &&
                status == room.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, status, ID);
    }

}
