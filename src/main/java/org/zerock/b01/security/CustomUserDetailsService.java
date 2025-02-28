package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.dto.MemberSecurityDTO;
import org.zerock.b01.repository.MemberRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  //  private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername : " + username);

        // DB에 등록된 사용자 정보를 불러오기.
       Optional<Member> result = memberRepository.getWithRoles(username);

       // 결과가 없는 경우에 UserDetails에 있는 예외 처리 클래스를 호출
       if(result.isEmpty()){
           throw new UsernameNotFoundException("username not found......."); //스프링 시큐리티가 가져온 예외

       }

       Member member = result.get();

        //UserDetails 개겣로 반환하는 userDetails를 생성..
        MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                member.getMid(),
                member.getMpw(),
                member.getEmail(),
                member.isDel(),
                false,  //소셜로 로그인 처리하지 않는 상황
                member.getRoleSet().stream().map(memberRole ->
                        new SimpleGrantedAuthority("ROLE_"+memberRole.name())).collect(Collectors.toList())
        );

        log.info("memberSecurityDTO : " + memberSecurityDTO);
        log.info(memberSecurityDTO);
        return memberSecurityDTO;
    }
}
