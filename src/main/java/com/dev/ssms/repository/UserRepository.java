package com.dev.ssms.repository;

import com.dev.ssms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUserSeqAndStatusSeq(String userSeq, Integer statusSeq);

    User findByEmailAndStatusSeq(String email, Integer statusSeq);
}