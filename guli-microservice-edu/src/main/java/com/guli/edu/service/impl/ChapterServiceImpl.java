package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.common.exception.GuliException;
import com.guli.edu.entity.Chapter;
import com.guli.edu.entity.Video;
import com.guli.edu.mapper.ChapterMapper;
import com.guli.edu.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.service.VideoService;
import com.guli.edu.vo.ChapterVo;
import com.guli.edu.vo.VideoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-02-25
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {
    @Autowired
    VideoService videoService;

    @Override
    public List<ChapterVo> nestedList(String courseId) {
        //最终要的到的数据列表
        List<ChapterVo> chapterVoList = new ArrayList<>();

        //获取章节信息
        QueryWrapper<Chapter> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("course_id", courseId);
        queryWrapper1.orderByAsc("sort", "id");
        List<Chapter> chapters = baseMapper.selectList(queryWrapper1);
        //获取视频信息
        QueryWrapper<Video> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("course_id", courseId);
        queryWrapper2.orderByAsc("sort", "id");
        List<Video> videos = videoService.list(queryWrapper2);
        //填充章节vo数据
        for (Chapter chapter : chapters) {
            //创建章节vo对象
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter, chapterVo);
            chapterVoList.add(chapterVo);
            //填充视频vo数据
            List<VideoVo> videoVoList = new ArrayList<>();

            for (Video video : videos) {
                if (chapter.getId().equals(video.getChapterId())) {
                    //创建视频vo对象
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video, videoVo);
                    videoVoList.add(videoVo);
                }
            }
            chapterVo.setChildren(videoVoList);
        }
        return chapterVoList;
    }

    @Override
    public boolean removeChapterById(String id) {
        if (videoService.getCountByChapterId(id)) {
            throw new GuliException(20001, "该分章节下存在视频课程，请先删除视频课");
        }
        Integer result = baseMapper.deleteById(id);

        return null != result && result > 0;
    }

    @Override
    public boolean removeByCourseId(String courseId) {
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        Integer count = baseMapper.delete(queryWrapper);
        return null != count && count > 0;
    }
}
