package com.guli.edu.controller.admin;

import com.guli.common.vo.R;
import com.guli.edu.entity.Video;
import com.guli.edu.form.VideoInfoForm;
import com.guli.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(description = "课时管理")
@RequestMapping("/admin/edu/video")
@CrossOrigin
public class VideoAdminController {
    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "新增课时")
    @PostMapping("save-video-info")
    public R saveVideoInfoForm(
            @ApiParam(name = "videoInfoForm", value = "课时信息对象", required = true)
            @RequestBody VideoInfoForm videoInfoForm) {
        videoService.saveVideoInfoForm(videoInfoForm);
        return R.ok();
    }

    @ApiOperation(value = "根据ID查询课时")
    @GetMapping("video-info/{id}")
    public R getVideoInfoById(
            @ApiParam(name = "id", value = "课时ID", required = true)
            @PathVariable String id) {
        VideoInfoForm videoInfoForm = videoService.getVideoInfoFormById(id);
        return R.ok().data("item",videoInfoForm);
    }

    @ApiOperation(value = "更新课时")
    @PutMapping("update-video-info/{id}")
    public R updateVideoInfoFormById(
            @ApiParam(name = "videoInfoForm", value = "课时信息对象", required = true)
            @RequestBody VideoInfoForm videoInfoForm,
            @ApiParam(name = "id", value = "课时ID", required = true)
            @PathVariable String id) {
        videoService.updateVideoInfoFormById(videoInfoForm);
        return R.ok();
    }
    @ApiOperation(value = "根据ID删除课时")
    @DeleteMapping("{id}")
    public R removeById(
            @ApiParam(name = "id", value = "课时ID", required = true)
            @PathVariable String id) {
        boolean result=videoService.removeVideoById(id);
        if (result){
            return R.ok();
        }else {
            return R.error().message("删除失败");
        }
    }

}
