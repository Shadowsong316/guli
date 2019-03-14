package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guli.common.constants.PriceConstants;
import com.guli.common.exception.GuliException;
import com.guli.edu.entity.Chapter;
import com.guli.edu.entity.Course;
import com.guli.edu.entity.CourseDescription;
import com.guli.edu.form.CourseInfoForm;
import com.guli.edu.mapper.CourseMapper;
import com.guli.edu.query.CourseQuery;
import com.guli.edu.service.ChapterService;
import com.guli.edu.service.CourseDescriptionService;
import com.guli.edu.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.service.VideoService;
import com.guli.edu.vo.CoursePublishVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Autowired
    private CourseDescriptionService courseDescriptionService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private ChapterService chapterService;

    @Override
    public boolean getCountBySubjectId(String subjectId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subject_id", subjectId);
        Integer count = baseMapper.selectCount(queryWrapper);
        return null != count && count > 0;
    }

    @Transactional
    @Override
    public String saveCourseInfo(CourseInfoForm courseInfoForm) {
        //保存课程基本信息
        Course course = new Course();
        course.setStatus(Course.COURSE_DRAFT);
        BeanUtils.copyProperties(courseInfoForm, course);
        boolean resultCourseInfo = this.save(course);
        if (!resultCourseInfo) {
            throw new GuliException(20001, "课程信息保存失败");
        }

        //保存课程详情信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setId(course.getId());
        courseDescription.setDescription(courseInfoForm.getDescription());
        boolean resultDescription = courseDescriptionService.save(courseDescription);
        if (!resultDescription) {
            throw new GuliException(20001, "课程详情信息保存失败");
        }
        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoFormById(String id) {

        Course course = baseMapper.selectById(id);
        if (course == null) {
            throw new GuliException(20001, "数据不存在");
        }
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course, courseInfoForm);

        CourseDescription courseDescription = courseDescriptionService.getById(id);
        if (courseDescription == null) {
            courseInfoForm.setDescription(" ");
        } else {
            courseInfoForm.setDescription(courseDescription.getDescription());
        }
        //设置显示精度：舍弃多余的位数
        courseInfoForm.setPrice(courseInfoForm.getPrice()
                .setScale(PriceConstants.DISPLAY_SCALE, BigDecimal.ROUND_FLOOR));
        return courseInfoForm;
    }

    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        boolean resultCourse = this.updateById(course);
        if (!resultCourse) {
            throw new GuliException(20001, "课程基本信息更新失败");
        }
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setId(courseInfoForm.getId());
        courseDescription.setDescription(courseInfoForm.getDescription());
        boolean resultCourseDescription = courseDescriptionService.updateById(courseDescription);
        if (!resultCourseDescription) {
            throw new GuliException(20001, "课程详情信息更新失败");
        }
    }

    @Override
    public void pageQuery(Page<Course> pageParam, CourseQuery courseQuery) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("gmt_create");
        if (courseQuery == null) {
            baseMapper.selectPage(pageParam, queryWrapper);
            return;
        }
        String title = courseQuery.getTitle();
        String teacherId = courseQuery.getTeacherId();
        String subjectId = courseQuery.getSubjectId();
        String subjectParentId = courseQuery.getSubjectParentId();
        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title", title);
        }
        if (!StringUtils.isEmpty(teacherId)) {
            queryWrapper.eq("teacher_id", teacherId);
        }
        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.ge("subject_id", subjectId);
        }
        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.ge("subject_parent_id", subjectParentId);
        }
        baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public boolean removeCourseById(String id) {
        //根据ID删除所有视频
        videoService.removeByCourseId(id);
        //根据ID删除所有章节
        chapterService.removeByCourseId(id);
        //根据ID删除课程详情表
        courseDescriptionService.removeById(id);
        //删除封面 TODO 独立完成
        //根据ID删除课程
        Integer result = baseMapper.deleteById(id);
        return null != result && result > 0;
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        return baseMapper.getCoursePublishVoById(id);
    }

    @Override
    public boolean publishCourseById(String id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        Integer result = baseMapper.updateById(course);
        return null!=result &&result>0;
    }

    @Override
    public List<Course> getCourseByTeacherId(String id) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id",id);
        queryWrapper.orderByDesc("gmt_modified");
        return baseMapper.selectList(queryWrapper);
    }
}
