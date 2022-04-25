package com.smartcoupon.service.usercard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCardRepository extends JpaRepository <UserCard, Long> {
        List<UserCard> findByUserEmailAndIsSmart(String email, boolean b);
        Optional<UserCard> findById(long id);
}
