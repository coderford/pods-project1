package com.CabCompany.RideService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
*/
@SpringBootApplication
public class RideServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideServiceApplication.class, args);
		//System.out.println("Hello people ...");

		CabDataService cabobject=new CabDataService();
		cabobject.Displaycabs();
		CustDataService custobject=new CustDataService();
		custobject.Displaycustomers();

		Cab data=cabobject.getAllCabs().get(1);
		
		data.setRideId(1996);
		cabobject.Displaycabs();


	}

}
