package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.Phone;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;

public record MemberUpdatePhoneRequest (
    @Phone(groups = PatternGroup.class)
    String phone
){

}
