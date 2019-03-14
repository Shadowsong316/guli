package com.guli.ucenter.mapper;

import com.guli.ucenter.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author Helen
 * @since 2019-03-12
 */
public interface MemberMapper extends BaseMapper<Member> {
    Integer countRegisterByDay(String day);
}
