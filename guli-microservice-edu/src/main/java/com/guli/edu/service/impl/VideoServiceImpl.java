package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.common.exception.GuliException;
import com.guli.edu.client.VodClient;
import com.guli.edu.entity.Video;
import com.guli.edu.form.VideoInfoForm;
import com.guli.edu.mapper.VideoMapper;
import com.guli.edu.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-02-25
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Autowired
    private VodClient vodClient;
    @Override
    public boolean getCountByChapterId(String chapterId) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chapter_id", chapterId);
        Integer count = baseMapper.selectCount(queryWrapper);
        return null != count && count > 0;
    }

    @Override
    public boolean removeByCourseId(String courseId) {
        //根据courseId删除阿里云上的视频记录
        QueryWrapper<Video> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("course_id",courseId);
        queryWrapper1.select("video_source_id");
        List<Video> videoList = baseMapper.selectList(queryWrapper1);

        List<String> videoSourceList = new ArrayList<>();
        for (Video video : videoList) {
            String videoSourceId = video.getVideoSourceId();
            if (!StringUtils.isEmpty(videoSourceId)){
                videoSourceList.add(videoSourceId);
            }
        }
        if (videoSourceList.size()>0){
            vodClient.removeVideoList(videoSourceList);
        }

        //根据courseId删除video表中的记录
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        Integer count = baseMapper.delete(queryWrapper);
        return null != count && count > 0;
    }

    @Override
    public void saveVideoInfoForm(VideoInfoForm videoInfoForm) {

        Video video = new Video();
        BeanUtils.copyProperties(videoInfoForm, video);
        boolean result = this.save(video);
        if (!result) {
            throw new GuliException(20001, "课时信息保存失败");
        }
    }

    @Override
    public VideoInfoForm getVideoInfoFormById(String id) {
        Video video = this.getById(id);
        if (video == null) {
            throw new GuliException(20001, "数据不存在");
        }
        VideoInfoForm videoInfoForm = new VideoInfoForm();
        BeanUtils.copyProperties(video, videoInfoForm);

        return videoInfoForm;
    }

    @Override
    public void updateVideoInfoFormById(VideoInfoForm videoInfoForm) {
        Video video = new Video();
        BeanUtils.copyProperties(videoInfoForm, video);
        boolean result = this.updateById(video);
        if (!result) {
            throw new GuliException(20001, "课时信息更新失败");
        }
    }
    @Transactional
    @Override
    public boolean removeVideoById(String id) {

        //查询云端视频id
        Video video = baseMapper.selectById(id);
        String videoSourceId = video.getVideoSourceId();
        //删除视频资源
        if(!StringUtils.isEmpty(videoSourceId)){
            vodClient.removeVideo(videoSourceId);
        }

        Integer result = baseMapper.deleteById(id);
        return null != result && result > 0;
    }
}
