package com.classpi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classpi.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiMessageMapper extends BaseMapper<AiMessage> {
    
    @Select("SELECT * FROM ai_message WHERE chat_id = #{chatId} ORDER BY create_time ASC")
    List<AiMessage> findByChatId(Integer chatId);
    
    @Select("SELECT COUNT(*) FROM ai_message WHERE chat_id = #{chatId}")
    Integer countByChatId(Integer chatId);
}