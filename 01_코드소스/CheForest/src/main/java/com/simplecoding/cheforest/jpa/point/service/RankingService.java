package com.simplecoding.cheforest.jpa.point.service;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final MemberRepository memberRepository;

    // 상위 10명 랭킹
    public List<Member> getTopRanking() {
        return memberRepository.findTop5ByRoleOrderByPointDesc(Member.Role.USER);
    }

    // 내 순위
    public Long getMyRank(Member member) {
        return memberRepository.findMyRank(member.getPoint());
    }
}
