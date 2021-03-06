package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.util.ExcelImportUtil;
import com.guli.edu.entity.Subject;
import com.guli.edu.mapper.SubjectMapper;
import com.guli.edu.service.CourseService;
import com.guli.edu.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.vo.SubjectNestedVo;
import com.guli.edu.vo.SubjectVo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-02-25
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {
    @Autowired
    private CourseService courseService;
    @Transactional
    @Override
    public List<String> batchImport(MultipartFile file) {
        List<String> msg = new ArrayList<>();

        //得到输入流
        try {
            InputStream inputStream = file.getInputStream();
            //使用工具类读取Excel
            ExcelImportUtil excelImportUtil = new ExcelImportUtil(inputStream);
            HSSFSheet sheet = excelImportUtil.getSheet();
            //得到数据行数
            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount <= 1) {
                msg.add("请编写数据");
                return msg;
            }
            //遍历数据行 从1开始
            for (int rowNum = 1; rowNum < rowCount; rowNum++) {
                HSSFRow rowData = sheet.getRow(rowNum);
                if (rowData != null) {

                    //获取单元格(一级分类)
                    String parentId = null;
                    String levelOneValue = "";
                    HSSFCell levelOneCell = rowData.getCell(0);
                    if (levelOneCell != null) {
                        //获取单元格的值
                        levelOneValue = excelImportUtil.getCellValue(levelOneCell);
                        if (StringUtils.isEmpty(levelOneValue)) {
                            msg.add("第" + rowNum + "行一级分类列为空");
                            continue;
                        }
                    }
                    Subject subject = this.getByTitle(levelOneValue);

                    if (subject == null) {//创建一级分类数据
                        Subject subjectLevelOne = new Subject();
                        subjectLevelOne.setTitle(levelOneValue);
                        subjectLevelOne.setSort(0);
                        baseMapper.insert(subjectLevelOne);
                        parentId = subjectLevelOne.getId();
                    } else {

                        parentId = subject.getId();
                    }
                    //获取单元格(二级分类)
                    String levelTwoValue = "";
                    HSSFCell levelTwoCell = rowData.getCell(1);
                    if (levelTwoCell != null) {
                        //获取单元格的值
                        levelTwoValue = excelImportUtil.getCellValue(levelTwoCell);
                        if (StringUtils.isEmpty(levelTwoValue)) {
                            msg.add("第" + rowNum + "行二级分类列为空");
                            continue;
                        }
                    }
                    Subject subjectSub = this.getSubByTitle(levelTwoValue, parentId);
                    if (subjectSub == null) {//创建二级分类数据
                        Subject subjectLevelTwo = new Subject();
                        subjectLevelTwo.setTitle(levelTwoValue);
                        subjectLevelTwo.setParentId(parentId);
                        subjectLevelTwo.setSort(0);
                        baseMapper.insert(subjectLevelTwo);
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(ResultCodeEnum.EXCEL_DATA_IMPORT_ERROR);
        }
        return msg;
    }

    @Override
    public List<SubjectNestedVo> nestedList() {
        //最终要获取的数据列表
        List<SubjectNestedVo> subjectNestedVoList=new ArrayList<>();
        //查找所有一级分类节点(DB)
        QueryWrapper<Subject> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("parent_id","0");
        queryWrapper1.orderByAsc("sort","id");
        List<Subject> subjects = baseMapper.selectList(queryWrapper1);

        //查找所有二级分类节点(DB)
        QueryWrapper<Subject> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.ne("parent_id","0");
        queryWrapper2.orderByAsc("sort","id");
        List<Subject> subSubjects = baseMapper.selectList(queryWrapper2);

        //填充一级分类的vo对象
        int count = subjects.size();
        for (int i = 0; i < count; i++) {
            Subject subject = subjects.get(i);
            //创建前端vo一级分类对象
            SubjectNestedVo subjectNestedVo = new SubjectNestedVo();
            BeanUtils.copyProperties(subject,subjectNestedVo);
            subjectNestedVoList.add(subjectNestedVo);
            //填充二级分类的vo对象
            ArrayList<SubjectVo> subjectVoArrayList = new ArrayList<>();
            int count2 = subSubjects.size();
            for (int j = 0; j < count2; j++) {
                Subject subSubject = subSubjects.get(j);
                if(subject.getId().equals(subSubject.getParentId())){
                    //创建二级分类vo对象
                    SubjectVo subjectVo = new SubjectVo();
                    BeanUtils.copyProperties(subSubject,subjectVo);
                    subjectVoArrayList.add(subjectVo);
                }
            }
            subjectNestedVo.setChildren(subjectVoArrayList);

        }

        return subjectNestedVoList;
    }

    @Override
    public boolean removeSubjectById(String id) {
        //根据当前id判断是否有子类别，如果有则提示用户
        if(this.getCountBySubjectId(id)){
            throw new GuliException(20001,"该分类下存在二级分类，请先删除子分类");
        }
        //根据当前id判断是否有课程信息，如果有则提示用户
        if (courseService.getCountBySubjectId(id)){
            throw new GuliException(20001,"该分类下存在课程，请先删除课程");

        }

        Integer result = baseMapper.deleteById(id);
        return null !=result &&result>0;
    }

    @Override
    public boolean saveLevelOne(Subject subject) {
        Subject subjectLevelOne = this.getByTitle(subject.getTitle());
        if (subjectLevelOne==null){
            return super.save(subject);
        }else {
            throw new GuliException(20001,"类别已存在");
        }
    }

    @Override
    public boolean saveLevelTwo(Subject subject) {
        Subject subjectLevelTwo = this.getSubByTitle(subject.getTitle(), subject.getParentId());
        if (subjectLevelTwo==null){
            return super.save(subject);
        }else {
            throw new GuliException(20001,"类别已存在");
        }
    }

    /**
     * 辅助方法
     * 根据一级分类的名称查询此分类是否存在
     *
     * @param title
     * @return
     */
    private Subject getByTitle(String title) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", 0);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 辅助方法
     * 根据二级分类的名称和父id查询此分类是否存在
     *
     * @param title
     * @param parentId
     * @return
     */
    private Subject getSubByTitle(String title, String parentId) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", parentId);
        return baseMapper.selectOne(queryWrapper);
    }
    /**
     * 辅助方法
     * 根据二级分类的名称和父id查询此分类是否存在
     *
     * @param parentId
     * @return
     */
    private boolean getCountBySubjectId(String parentId) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        Integer count = baseMapper.selectCount(queryWrapper);
        return null !=count && count>0;
    }
}
