package com.CabCompany.RideService;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NextRideIdRepo extends CrudRepository<NextRideId, Integer> {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<NextRideId> findById(Integer id);
}
