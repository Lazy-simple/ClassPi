package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classpi.common.Result;
import com.classpi.entity.Preparation;
import com.classpi.mapper.PreparationMapper;
import com.classpi.service.PreparationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PreparationServiceImpl implements PreparationService {

    @Autowired
    private PreparationMapper preparationMapper;

    @Override
    public Result addPreparation(String teacherId, String teacherName, String title, String type, String content) {
        Preparation preparation = new Preparation();
        preparation.setTeacherId(teacherId);
        preparation.setTeacherName(teacherName);
        preparation.setTitle(title);
        preparation.setType(type);
        preparation.setContent(content);
        preparation.setCourseId(null);
        preparation.setCourseNo(null);
        preparation.setCreateTime(new Date());
        preparation.setUpdateTime(new Date());
        preparation.setDeleted(0);
        
        int result = preparationMapper.insert(preparation);
        if (result > 0) {
            return Result.success("新增备课内容成功", preparation);
        }
        return Result.error("新增备课内容失败");
    }

    @Override
    public Result deletePreparation(Integer id, String teacherId, String identity) {
        LambdaQueryWrapper<Preparation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preparation::getId, id)
                    .eq(Preparation::getTeacherId, teacherId)
                    .eq(Preparation::getDeleted, 0);
        
        Preparation preparation = preparationMapper.selectOne(queryWrapper);
        if (preparation == null) {
            return Result.error("备课内容不存在或无权限");
        }
        
        preparation.setDeleted(1);
        preparation.setUpdateTime(new Date());
        int result = preparationMapper.updateById(preparation);
        if (result > 0) {
            return Result.success("删除备课内容成功");
        }
        return Result.error("删除备课内容失败");
    }

    @Override
    public Result updatePreparation(Integer id, String title, String content, String teacherId, String identity) {
        LambdaQueryWrapper<Preparation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preparation::getId, id)
                    .eq(Preparation::getTeacherId, teacherId)
                    .eq(Preparation::getDeleted, 0);
        
        Preparation preparation = preparationMapper.selectOne(queryWrapper);
        if (preparation == null) {
            return Result.error("备课内容不存在或无权限");
        }
        
        preparation.setTitle(title);
        preparation.setContent(content);
        preparation.setUpdateTime(new Date());
        int result = preparationMapper.updateById(preparation);
        if (result > 0) {
            return Result.success("修改备课内容成功", preparation);
        }
        return Result.error("修改备课内容失败");
    }

    @Override
    public Result getTeacherPreparations(String teacherId, Long page, Long pageSize) {
        Page<Preparation> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Preparation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preparation::getTeacherId, teacherId)
                    .eq(Preparation::getDeleted, 0)
                    .orderByDesc(Preparation::getCreateTime);
        
        Page<Preparation> resultPage = preparationMapper.selectPage(pageParam, queryWrapper);
        return Result.success("获取教师备课列表成功", resultPage);
    }

    @Override
    public Result getTeacherPreparationsByType(String teacherId, String type, Long page, Long pageSize) {
        Page<Preparation> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Preparation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preparation::getTeacherId, teacherId)
                    .eq(Preparation::getType, type)
                    .eq(Preparation::getDeleted, 0)
                    .orderByDesc(Preparation::getCreateTime);
        
        Page<Preparation> resultPage = preparationMapper.selectPage(pageParam, queryWrapper);
        return Result.success("按类型获取教师备课列表成功", resultPage);
    }

    @Override
    public Result getUnassignedPreparations(String teacherId, Long page, Long pageSize) {
        Page<Preparation> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Preparation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preparation::getTeacherId, teacherId)
                    .isNull(Preparation::getCourseId)
                    .eq(Preparation::getDeleted, 0)
                    .orderByDesc(Preparation::getCreateTime);
        
        Page<Preparation> resultPage = preparationMapper.selectPage(pageParam, queryWrapper);
        return Result.success("获取未分配课程的备课列表成功", resultPage);
    }

    @Override
    public Result assignToCourse(Integer id, Integer courseId, String courseNo, String teacherId, String identity) {
        LambdaQueryWrapper<Preparation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preparation::getId, id)
                    .eq(Preparation::getTeacherId, teacherId)
                    .eq(Preparation::getDeleted, 0);
        
        Preparation preparation = preparationMapper.selectOne(queryWrapper);
        if (preparation == null) {
            return Result.error("备课内容不存在或无权限");
        }
        
        preparation.setCourseId(courseId);
        preparation.setCourseNo(courseNo);
        preparation.setUpdateTime(new Date());
        int result = preparationMapper.updateById(preparation);
        if (result > 0) {
            return Result.success("备课内容分配课程成功", preparation);
        }
        return Result.error("备课内容分配课程失败");
    }

    @Override
    public Result getPreparationById(Integer id, String teacherId, String identity) {
        LambdaQueryWrapper<Preparation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preparation::getId, id)
                    .eq(Preparation::getTeacherId, teacherId)
                    .eq(Preparation::getDeleted, 0);
        
        Preparation preparation = preparationMapper.selectOne(queryWrapper);
        if (preparation == null) {
            return Result.error("备课内容不存在或无权限");
        }
        return Result.success("获取备课详情成功", preparation);
    }
}
