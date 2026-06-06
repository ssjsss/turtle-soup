package com.turtlesoup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.turtlesoup.entity.GameSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameSessionMapper extends BaseMapper<GameSession> {
}
