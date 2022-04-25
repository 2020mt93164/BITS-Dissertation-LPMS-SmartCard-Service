package com.smartcoupon.service.smartcard;

import com.smartcoupon.service.usercard.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmartCardRepository extends JpaRepository<UserCard,Long> {
       List<UserCard> findByUserEmailAndIsSmart(String s, boolean b);
}
