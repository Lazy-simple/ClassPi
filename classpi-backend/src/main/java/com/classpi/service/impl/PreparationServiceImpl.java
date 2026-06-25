package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classpi.common.PageResult;
import com.classpi.common.Result;
import com.classpi.entity.Preparation;
import com.classpi.mapper.PreparationMapper;
import com.classpi.service.PreparationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PreparationServiceImpl implements PreparationService {

    @Autowired
    private PreparationMapper preparationMapper;

    @Override
    @Transactional
    public Result addPreparation(String teacherId, String teacherName, String title, String type, String content) {
        Preparation preparation = new Preparation();
        preparation.setTeacherId(teacherId);
        preparation.setTeacherName(teacherName);
        preparation.setTitle(title);
        preparation.setType(type);
        preparation.setContent(content);

        preparationMapper.insert(preparation);
        return Result.success("备课内容添加成功", preparation);
    }

    @Override
    @Transactional
    public Result deletePreparation(Integer id, String teacherId, String identity) {
        Preparation preparation = preparationMapper.selectById(id);
        if (preparation == null) {
            return Result.error("备课内容不存在");
        }

        if (!"teacher".equalsIgnoreCase(identity) && !preparation.getTeacherId().equals(teacherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除他人的备课内容");
        }

        preparationMapper.deleteById(id);
        return Result.success("备课内容删除成功");
    }

    @Override
    @Transactional
    public Result updatePreparation(Integer id, String title, String content, String teacherId, String identity) {
        Preparation preparation = preparationMapper.selectById(id);
        if (preparation == null) {
            return Result.error("备课内容不存在");
        }

        if (!"teacher".equalsIgnoreCase(identity) && !preparation.getTeacherId().equals(teacherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改他人的备课内容");
        }

        preparation.setTitle(title);
        preparation.setContent(content);
        preparationMapper.updateById(preparation);
        return Result.success("备课内容更新成功", preparation);
    }

    @Override
    public Result getTeacherPreparations(String teacherId, Long page, Long pageSize) {
        QueryWrapper<Preparation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.orderByDesc("create_time");
        
        Page<Preparation> pageObj = new Page<>(page, pageSize);
        Page<Preparation> resultPage = preparationMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Preparation> pageResult = PageResult.of(
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize(),
            resultPage.getRecords()
        );
        
        return Result.success("获取备课列表成功", pageResult);
    }

    @Override
    public Result getTeacherPreparationsByType(String teacherId, String type, Long page, Long pageSize) {
        QueryWrapper<Preparation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.eq("type", type);
        queryWrapper.orderByDesc("create_time");
        
        Page<Preparation> pageObj = new Page<>(page, pageSize);
        Page<Preparation> resultPage = preparationMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Preparation> pageResult = PageResult.of(
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize(),
            resultPage.getRecords()
        );
        
        return Result.success("获取备课列表成功", pageResult);
    }

    @Override
    public Result getUnassignedPreparations(String teacherId, Long page, Long pageSize) {
        QueryWrapper<Preparation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.isNull("course_id");
        queryWrapper.orderByDesc("create_time");
        
        Page<Preparation> pageObj = new Page<>(page, pageSize);
        Page<Preparation> resultPage = preparationMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Preparation> pageResult = PageResult.of(
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize(),
            resultPage.getRecords()
        );
        
        return Result.success("获取未分配备课列表成功", pageResult);
    }

    @Override
    @Transactional
    public Result assignToCourse(Integer id, Integer courseId, String courseNo, String teacherId, String identity) {
        Preparation preparation = preparationMapper.selectById(id);
        if (preparation == null) {
            return Result.error("备课内容不存在");
        }

        if (!"teacher".equalsIgnoreCase(identity) && !preparation.getTeacherId().equals(teacherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权分配他人的备课内容");
        }

        preparation.setCourseId(courseId);
        preparation.setCourseNo(courseNo);
        preparationMapper.updateById(preparation);
        return Result.success("备课内容分配成功", preparation);
    }

    @Override
    public Result getPreparationById(Integer id, String teacherId, String identity) {
        Preparation preparation = preparationMapper.selectById(id);
        if (preparation == null) {
            return Result.error("备课内容不存在");
        }

        if (!"teacher".equalsIgnoreCase(identity) && !preparation.getTeacherId().equals(teacherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权查看他人的备课内容");
        }
        
        return Result.success("获取备课详情成功", preparation);
    }
}
