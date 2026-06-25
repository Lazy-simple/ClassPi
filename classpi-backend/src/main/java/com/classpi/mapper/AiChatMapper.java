package com.classpi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classpi.entity.AiChat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiChatMapper extends BaseMapper<AiChat> {
    
    @Select("SELECT * FROM ai_chat WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<AiChat> findByUserId(String userId);
}