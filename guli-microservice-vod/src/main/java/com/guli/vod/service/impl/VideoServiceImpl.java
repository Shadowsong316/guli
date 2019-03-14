package com.guli.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.*;
import com.guli.common.exception.GuliException;
import com.guli.vod.service.VideoService;
import com.guli.vod.util.AliyunVodSDKUtils;
import com.guli.vod.util.ConstantPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Override
    public String uploadVideo(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            UploadStreamRequest request = new UploadStreamRequest(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET,
                    title, originalFilename, inputStream);
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
            // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            String videoId = response.getVideoId();
            if (!response.isSuccess()) {
                String errorMessage = "阿里云上传错误：" + "code：" + response.getCode() + ", message：" + response.getMessage();
                log.warn(errorMessage);
                if (StringUtils.isEmpty(videoId)) {
                    throw new GuliException(20001, errorMessage);
                }
            }

            return videoId;
        } catch (IOException e) {
            throw new GuliException(20001, "guli vod 服务上传失败");
        }

    }

    @Override
    public void removeVideo(String videoId) {
        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(videoId);
            DeleteVideoResponse response = client.getAcsResponse(request);
            System.out.println("RequestId = " + response.getRequestId() + "\n");

        } catch (ClientException e) {
            throw new GuliException(20001, "视频删除失败");
        }
    }

    @Override
    public CreateUploadVideoResponse getUploadAuthAndAddress(String title, String fileName) {
        try {
            //初始化
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            //创建请求对象
            CreateUploadVideoRequest request = new CreateUploadVideoRequest();
            request.setTitle(title);
            request.setFileName(fileName);
            //获取响应
            CreateUploadVideoResponse response = client.getAcsResponse(request);
            return response;
        } catch (ClientException e) {
            throw new GuliException(20001, "获取视频上传地址和凭证失败");
        }
    }

    @Override
    public RefreshUploadVideoResponse refreshUploadAuth(String videoId) {
        try {
            //初始化
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(ConstantPropertiesUtil.ACCESS_KEY_ID, ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            //创建请求对象
            RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
            request.setVideoId(videoId);
            //获取响应
            RefreshUploadVideoResponse response = client.getAcsResponse(request);
            return response;
        } catch (ClientException e) {
            throw new GuliException(20001, "刷新视频上传地址和凭证成功");
        }
    }

    @Override
    public void removeVideoList(List<String> videoIdList) {
        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            DeleteVideoRequest request = new DeleteVideoRequest();
            String joinStr = org.apache.commons.lang.StringUtils.join(videoIdList.toArray(), ",");
            request.setVideoIds(joinStr);
            DeleteVideoResponse response = client.getAcsResponse(request);
            System.out.println("RequestId = " + response.getRequestId() + "\n");

        } catch (ClientException e) {
            throw new GuliException(20001, "视频删除失败");
        }
    }
}
