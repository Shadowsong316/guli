package com.guli.statistics.controller.admin;


import com.guli.common.vo.R;
import com.guli.statistics.service.DailyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2019-03-13
 */
@Api(description = "统计分析服务")
@CrossOrigin
@RestController
@RequestMapping("/admin/statistics/daily")
public class DailyAdminController {

    @Autowired
    private DailyService dailyService;

    @PostMapping("{day}")
    public R createStatisticsByDate(@PathVariable String day) {
        dailyService.createStatisticsByDay(day);
        return R.ok();
    }
    @GetMapping("show-chart/{begin}/{end}/{type}")
    public R showChart(@PathVariable String begin,
                       @PathVariable String end,
                       @PathVariable String type){
        Map<String,Object> map=dailyService.getChartData(begin,end,type);
        return R.ok().data(map);
    }
}

