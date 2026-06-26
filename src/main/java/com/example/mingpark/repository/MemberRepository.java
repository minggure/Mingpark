package com.example.mingpark.repository;

import com.example.mingpark.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 회원 정보 데이터베이스 접근 리포지토리.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * 로그인 ID 중복 여부 확인 처리.
     *
     * @param loginId 검증 대상 로그인 ID
     * @return 존재 시 true, 미존재 시 false 반환
     */
    boolean existsByLoginId(String loginId);
    /**
     * 로그인 ID 기반 회원 정보 단일 조회.
     *
     * @param loginId 조회 대상 로그인 ID
     * @return 회원 엔티티 객체를 포함한 Optional 객체 반환
     */
    Optional<Member> findByLoginId(String loginId);
}