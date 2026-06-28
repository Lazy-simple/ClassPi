package com.classpi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classpi.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
