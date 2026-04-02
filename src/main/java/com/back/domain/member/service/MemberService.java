package com.back.domain.member.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// 회원 관련 비즈니스 규칙을 모으는 서비스 계층
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public Member join(String username, String password, String nickname) {
        return join(username, password, nickname, UUID.randomUUID().toString());
    }
    public Member join(String username, String password, String nickname, String apiKey) {

        findByUsername(username).ifPresent(
                m -> {
                    throw new ServiceException("409-1", "이미 사용중인 아이디입니다.");
                }
        );

        Member member = new Member(username, passwordEncoder.encode(password), nickname, apiKey);
        return memberRepository.save(member);
    }

    public long count() {
        return memberRepository.count();
    }

    public Optional<Member> findByUsername(String username) {
        // Spring Data JPA가 메서드 이름만으로 username 조회 쿼리를 만들어준다.
//        return memberRepository.findAll().stream()
//                .filter(m -> m.getUsername().equals(username))
//                .findFirst();
//        같은 코드
        return memberRepository.findByUsername(username); // DB에서 가져오는 코드
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }
    public Map<String, Object> payloadOrNull(String jwt) {
        return authTokenService.payloadOrNull(jwt);
    }

    public Optional<Member> findById(int id) {
        return memberRepository.findById(id);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public void checkPassword(String inputPassword, String rawPassword) {
        if(!passwordEncoder.matches(inputPassword, rawPassword)) {
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }
    }
}
