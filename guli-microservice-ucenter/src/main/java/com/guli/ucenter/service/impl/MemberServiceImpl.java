package com.guli.ucenter.service.impl;

import com.guli.ucenter.entity.Member;
import com.guli.ucenter.mapper.MemberMapper;
import com.guli.ucenter.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-03-12
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Override
    public Integer countRegisterByDay(String day) {
        return baseMapper.countRegisterByDay(day);
    }
}
