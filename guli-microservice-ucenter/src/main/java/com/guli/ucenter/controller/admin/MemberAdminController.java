package com.guli.ucenter.controller.admin;


import com.guli.common.vo.R;
import com.guli.ucenter.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2019-03-12
 */
@Api(description = "用户中心")
@CrossOrigin
@RestController
@RequestMapping("/admin/ucenter/member")
public class MemberAdminController {
    @Autowired
    private MemberService memberService;
    @ApiOperation(value = "今日注册数")
    @GetMapping(value = "count-register/{day}")
    public R countRegister(
            @ApiParam(name = "day",value = "日期",required = true)
            @PathVariable String day){
        Integer count=memberService.countRegisterByDay(day);
        return R.ok().data("countRegister",count);
    }
}

