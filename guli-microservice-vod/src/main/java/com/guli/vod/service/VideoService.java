package com.guli.vod.service;

import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    String uploadVideo(MultipartFile file);

    void removeVideo(String videoId);

    CreateUploadVideoResponse getUploadAuthAndAddress(String title, String fileName);

    RefreshUploadVideoResponse refreshUploadAuth(String videoId);

    void removeVideoList(List<String> videoIdList);
}
