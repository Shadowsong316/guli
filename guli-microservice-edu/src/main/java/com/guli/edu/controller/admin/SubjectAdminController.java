package com.guli.edu.controller.admin;

import com.guli.common.vo.R;
import com.guli.edu.entity.Subject;
import com.guli.edu.service.SubjectService;
import com.guli.edu.vo.SubjectNestedVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(description = "课程分类管理")
@CrossOrigin
@RequestMapping("/admin/edu/subject")
@RestController
public class SubjectAdminController {
    @Autowired
    private SubjectService subjectService;
    @PostMapping("import")
    @ApiOperation(value = "Excel批量数据导入")
    public R importExcel(
            @ApiParam(name = "file",value = "Excel表格",required = true)
            @RequestParam MultipartFile file) throws Exception{
        List<String> msg=subjectService.batchImport(file);
        if (msg.size()==0){
            return R.ok().message("批量导入成功");
        }else {
            return R.error().message("部分数据导入失败").data("messageList",msg);
        }
    }
    @ApiOperation(value = "嵌套数据列表")
    @GetMapping("")
    public R nestedList(){
        List<SubjectNestedVo> subjectNestedVoList=subjectService.nestedList();
        return R.ok().data("items",subjectNestedVoList);
    }
    @ApiOperation(value = "根据ID删除类别")
    @DeleteMapping("{id}")
    public R removeById(
            @ApiParam(name = "id",value = "类别ID",required = true)
            @PathVariable String id){
        boolean result = subjectService.removeSubjectById(id);
        if (result){
            return R.ok();
        }else {
            return R.error().message("您删除的记录不存在");
        }
    }
    @ApiOperation(value = "保存一级分类")
    @PostMapping("save-level-one")
    public R saveLevelOne(
            @ApiParam(name = "subject",value = "分类对象",required = true)
            @RequestBody Subject subject){
        boolean result=subjectService.saveLevelOne(subject);
        if (result){
            return R.ok();
        }else {
            return R.error().message("保存失败");
        }
    }
    @ApiOperation(value = "保存二级分类")
    @PostMapping("save-level-two")
    public R saveLevelTwo(
            @ApiParam(name = "subject",value = "分类对象",required = true)
            @RequestBody Subject subject){
        boolean result=subjectService.saveLevelTwo(subject);
        if (result){
            return R.ok();
        }else {
            return R.error().message("保存失败");
        }
    }
}
