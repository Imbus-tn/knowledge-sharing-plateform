package com.imbus.knowledge.User_Management.repositories;

import com.imbus.knowledge.User_Management.entities.ForgotPassword;
import com.imbus.knowledge.User_Management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {

    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM ForgotPassword fp WHERE fp.user = ?1")
    void deleteByUser(User user); // Delete existing OTP records for a user
}
