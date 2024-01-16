package com.yanolja.scbj.domain.member.service;

import com.yanolja.scbj.domain.member.dto.request.MemberSignInRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdateAccountRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.MemberResponse;
import com.yanolja.scbj.domain.member.dto.response.MemberSignInResponse;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.exception.AlreadyExistEmailException;
import com.yanolja.scbj.domain.member.exception.InvalidEmailAndPasswordException;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.exception.NotFoundYanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.member.repository.YanoljaMemberRepository;
import com.yanolja.scbj.domain.member.util.MemberMapper;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final YanoljaMemberRepository yanoljaMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final JwtUtil jwtUtil;


    MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
        SecurityUtil securityUtil, JwtUtil jwtUtil,
        YanoljaMemberRepository yanoljaMemberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
        this.jwtUtil = jwtUtil;
        this.yanoljaMemberRepository = yanoljaMemberRepository;
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
            .orElseThrow(() -> new InvalidEmailAndPasswordException(ErrorCode.INVALID_EMAIL_AND_PASSWORD));

        if (passwordEncoder.matches(memberSignInRequest.password(), retrivedMember.getPassword())) {
            String accessToken = jwtUtil.generateToken(MemberMapper.toUserDetails(retrivedMember));
            String refreshToken = jwtUtil.generateRefreshToken(
                String.valueOf(retrivedMember.getId()));
            return MemberMapper.toSignInResponse(MemberMapper.toMemberResponse(retrivedMember),
                MemberMapper.toTokenResponse(accessToken, refreshToken));
        } else {
            throw new InvalidEmailAndPasswordException(ErrorCode.INVALID_EMAIL_AND_PASSWORD);
        }
    }


    public void logout(final RefreshRequest refreshRequest) {
        jwtUtil.setBlackList(refreshRequest.getAccessToken().substring(JwtUtil.GRANT_TYPE.length()),
            refreshRequest.getRefreshToken());
    }

    public void updateMemberPassword(
        final MemberUpdatePasswordRequest memberUpdatePasswordRequest) {
        Member retrivedMember = memberRepository.findByEmail(memberUpdatePasswordRequest.email())
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        retrivedMember.updatePassword(
            passwordEncoder.encode(memberUpdatePasswordRequest.password()));
    }

    private void checkPassword(String email, String password) {
        String[] splitedEmail = email.split("@");
        if (email.equals(password) || splitedEmail[0].equals(password) || splitedEmail[1].equals(
            password) || email.replaceAll("@", "").equals(password)) {
            throw new InvalidEmailAndPasswordException(ErrorCode.INVALID_EMAIL_AND_PASSWORD);
        }
    }

    public void updateMemberAccount(final MemberUpdateAccountRequest memberUpdateAccountRequest) {
        getCurrentMember().updateAccount(memberUpdateAccountRequest.accountNumber(),
            memberUpdateAccountRequest.bank());
    }

    public void updateMemberName(final String nameToUpdate) {
        getCurrentMember().updateName(nameToUpdate);
    }

    public void linkUpYanolja(final String yanoljaEmail) {
        YanoljaMember yanoljaMember = yanoljaMemberRepository.findByEmail(yanoljaEmail)
            .orElseThrow(() -> new NotFoundYanoljaMember(ErrorCode.NOT_FOUND_YANOLJA_MEMBER));
        getCurrentMember().setYanoljaMember(yanoljaMember);
    }

    public void updateMemberPhone(final String phoneToUpdate) {
        getCurrentMember().updatePhone(phoneToUpdate);
    }

    public MemberResponse getMemberInfo() {
        return MemberMapper.toMemberResponse(getCurrentMember());
    }

    private Member getCurrentMember() {
        return memberRepository.findById(securityUtil.getCurrentMemberId())
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Boolean isYanoljaLinkedUpMember() {
        return getCurrentMember().getYanoljaMember() != null;
    }

}
