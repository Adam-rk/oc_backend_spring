package com.oc.repository;

import com.oc.model.Message;
import com.oc.model.Rental;
import com.oc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByUser(User user);
    List<Message> findByRental(Rental rental);
    List<Message> findByUserAndRental(User user, Rental rental);
}
