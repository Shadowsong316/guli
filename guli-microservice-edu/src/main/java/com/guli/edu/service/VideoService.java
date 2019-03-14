package com.guli.edu.service;

import com.guli.edu.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.edu.form.VideoInfoForm;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author Helen
 * @since 2019-02-25
 */
public interface VideoService extends IService<Video> {

    boolean getCountByChapterId(String chapterId);

    boolean removeByCourseId(String courseId);

    void saveVideoInfoForm(VideoInfoForm videoInfoForm);

    VideoInfoForm getVideoInfoFormById(String id);

    void updateVideoInfoFormById(VideoInfoForm videoInfoForm);

    boolean removeVideoById(String id);
}
