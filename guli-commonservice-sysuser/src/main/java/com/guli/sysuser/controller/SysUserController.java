package com.guli.sysuser.controller;

import com.guli.common.vo.R;
import com.guli.sysuser.entity.Sysuser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

@Api("系统用户管理")
@RequestMapping("/admin/sysuser")
@CrossOrigin//跨域
@RestController
public class SysUserController {
    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    public R login(
            @ApiParam(name = "sysuser", value = "系统用户对象", required = true)
            @RequestBody Sysuser sysuser) {

        return R.ok().data("token", "admin");
    }

    @GetMapping("info")
    @ApiOperation(value = "获取用户信息")
    public R info(
            @ApiParam(name = "token", value = "令牌", required = true)
            @RequestParam String token) {

        return R.ok()
                .data("roles", "admin")
                .data("name", "admin")
                .data("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
    }
    @PostMapping("logout")
    @ApiOperation(value = "用户登出")
    public R logout(){
        return R.ok();
    }
}
