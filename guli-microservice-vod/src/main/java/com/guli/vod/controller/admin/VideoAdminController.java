package com.guli.vod.controller.admin;

import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoResponse;
import com.guli.common.vo.R;
import com.guli.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(description = "阿里云视频点播微服务")
@CrossOrigin //跨域
@RestController
@RequestMapping("/admin/vod/video")
public class VideoAdminController {
    @Autowired
    private VideoService videoService;

    @PostMapping("upload")
    public R uploadVideo(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam MultipartFile file) {
        String videoId = videoService.uploadVideo(file);
        return R.ok().data("videoId", videoId).message("视频上传成功");
    }

    @DeleteMapping("{videoId}")
    public R removeVideo(
            @ApiParam(name = "videoId", value = "视频ID", required = true)
            @PathVariable String videoId) {
                videoService.removeVideo(videoId);
                return R.ok().message("视频删除成功");
    }
    /**
     * 获取视频上传地址和凭证
     * @param title
     * @param fileName
     * @return
     */
    @GetMapping("get-upload-auth-and-address/{title}/{fileName}")
    public R getUploadAuthAndAddress(
            @ApiParam(name = "title",value = "视频标题",required = true)
            @PathVariable String title,
            @ApiParam(name = "fileName",value = "视频源文件名",required = true)
            @PathVariable String fileName){
        CreateUploadVideoResponse response=videoService.getUploadAuthAndAddress(title,fileName);
        return R.ok().message("获取视频上传地址和凭证成功").data("response",response);
    }

    /**
     * 刷新视频上传地址和凭证
     * @param videoId
     * @return
     */
    @GetMapping("refresh-upload-auth/{videoId}")
    public R refreshUploadAuth(
            @ApiParam(name = "videoId",value = "云端视频ID",required = true)
            @PathVariable String videoId){
        videoService.refreshUploadAuth(videoId);
        return R.ok().message("刷新视频上传地址和凭证成功");
    }

    /**
     * 批量删除视频
     * @param videoIdList
     * @return
     */
    @DeleteMapping("delete-batch")
    public R removeVideoList(
            @ApiParam(name = "videoIdList", value = "视频ID集合", required = true)
            @RequestParam("videoIdList") List videoIdList) {
        videoService.removeVideoList(videoIdList);
        return R.ok().message("视频删除成功");
    }
}
