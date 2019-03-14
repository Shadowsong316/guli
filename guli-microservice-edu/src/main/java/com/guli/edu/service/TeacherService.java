package com.guli.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guli.edu.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.edu.query.TeacherQuery;

import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author Helen
 * @since 2019-02-25
 */
public interface TeacherService extends IService<Teacher> {

    void pageQuery(Page<Teacher> pageParm, TeacherQuery teacherQuery);

    Map<String,Object> pageListWeb(Page<Teacher> pageParam);
}
