package com.CabCompany.RideService;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class NextRideId {
    @Id
    Integer dummyId;
    int rideId;
    
    public NextRideId() {
    }

    public NextRideId(int id) {
        dummyId = 0;
        rideId = id;
    }

    public int getRideId() {
        return rideId;
    }
}
