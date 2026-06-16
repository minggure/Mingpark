package com.example.mingpark.repository;

import com.example.mingpark.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository : 상속하면 save, findById, findAll, delete 등을 사용할 수 있다
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);

    Optional<Member> findByLoginId(String loginId);
}