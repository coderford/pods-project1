package com.CabCompany.RideService;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustRepo extends CrudRepository<Customer, Integer> {

}
