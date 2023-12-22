package com.yanolja.scbj.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final JwtUtil jwtUtil;


    MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
        SecurityUtil securityUtil, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
        this.jwtUtil = jwtUtil;
    }

    public MemberResponse signUp(final MemberSignUpRequest memberSignUpRequest) {
        checkPassword(memberSignUpRequest.email(), memberSignUpRequest.password());

        if (memberRepository.existsByEmail(memberSignUpRequest.email())) {
            throw new AlreadyExistEmailException(ErrorCode.ALREADY_EXIST_EMAIL);
        }
        return MemberMapper.toMemberResponse(memberRepository.save(
            MemberMapper.toMember(memberSignUpRequest,
                passwordEncoder.encode(memberSignUpRequest.password()))));
    }

    public MemberSignInResponse signIn(final MemberSignInRequest memberSignInRequest) {
        Member retrivedMember = memberRepository.findByEmail(memberSignInRequest.email())
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (passwordEncoder.matches(memberSignInRequest.password(), retrivedMember.getPassword())) {
            String accessToken = jwtUtil.generateToken(MemberMapper.toUserDetails(retrivedMember));
            String refreshToken = jwtUtil.generateRefreshToken(
                String.valueOf(retrivedMember.getId()));
            return MemberMapper.toSignInResponse(MemberMapper.toMemberResponse(retrivedMember),
                MemberMapper.toTokenResponse(accessToken, refreshToken));
        } else {
            throw new NotMatchPasswordException(ErrorCode.NOT_MATCH_PASSWORD);
        }
    }

    public void updateMemberPassword(
        final MemberUpdatePasswordRequest memberUpdatePasswordRequest) {
        Member member = memberRepository.findById(securityUtil.getCurrentMemberId())
            .orElseThrow(() -> new MemberNotFoundException(
                ErrorCode.MEMBER_NOT_FOUND));

        member.updatePassword(passwordEncoder.encode(memberUpdatePasswordRequest.password()));
    }

    private void checkPassword(String email, String password) {
        String[] splitedEmail = email.split("@");
        if (email.equals(password) || splitedEmail[0].equals(password) || splitedEmail[1].equals(
            password) || email.replaceAll("@", "").equals(password)) {
            throw new InvalidPasswordException(ErrorCode.INVALID_PASSWORD);
        }
    }

}
