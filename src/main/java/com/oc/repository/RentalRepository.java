package com.oc.repository;

import com.oc.model.Rental;
import com.oc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    List<Rental> findByOwner(User owner);
}
