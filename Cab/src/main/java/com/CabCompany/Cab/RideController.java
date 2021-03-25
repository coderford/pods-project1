package com.CabCompany.Cab;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.aop.support.annotation.*;

@RestController
public class RideController {


@RequestMapping(value="/RideService")
public String display()
{
	return "Welcome to spring boot Ride controller ";
}
}
