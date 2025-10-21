package com.simplecoding.cheforest.jpa.home.controller;

import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.auth.security.CustomOAuth2User;
import com.simplecoding.cheforest.jpa.auth.security.CustomUserDetails;
import com.simplecoding.cheforest.jpa.home.service.HomeService;
import com.simplecoding.cheforest.jpa.point.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RankingService rankingService;
    private final MemberRepository memberRepository;
    private final HomeService homeService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal Object principal, Model model) {

        // 홈 콘텐츠 (레시피/게시판)
        model.addAttribute("popularRecipes", homeService.getPopularRecipes());
        model.addAttribute("categoryRecipes", homeService.getCategoryTop3Recipes());
        model.addAttribute("categoryBoards", homeService.getCategoryLatestBoards());

        // 포인트 상위 5명
        List<Member> topMembers = rankingService.getTopRanking();
        model.addAttribute("topMembers", topMembers);

        // 로그인한 사용자 감지 (일반 + 소셜 모두)
        Member me = null;

        if (principal instanceof CustomUserDetails userDetails) {
            me = memberRepository.findById(userDetails.getMemberIdx()).orElse(null);
        } else if (principal instanceof CustomOAuth2User oauthUser) {
            me = memberRepository.findById(oauthUser.getMemberIdx()).orElse(null);
        }

        // 내 랭킹 계산
        if (me != null) {
            Long myRank = rankingService.getMyRank(me);
            log.info("내 랭킹 계산 결과: {}, 닉네임={}, 포인트={}, ROLE={}",
                    myRank, me.getNickname(), me.getPoint(), me.getRole());
            model.addAttribute("myRank", myRank);
        }
        return "home";
    }
}
