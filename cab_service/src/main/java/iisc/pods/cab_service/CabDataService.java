/*
    This is a spring service which keeps track of available cabs
    and returns whatever data is needed to methods in the controller.
*/
package iisc.pods.cab_service;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Service;

@Service
public class CabDataService {
    private ArrayList<Cab> cabs;

    public CabDataService() {
        cabs = new ArrayList<>(Arrays.asList(
            new Cab(1), new Cab(2), new Cab(3), new Cab(4)
        ));
    }

    public ArrayList<Cab> getAllCabs() {
        return cabs;
    }

    public Cab getCabWithId(int id) {
        return cabs.stream().filter(c -> (c.getId() == id)).findFirst().get();
    }
}
