package com.turtlesoup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.turtlesoup.entity.Puzzle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PuzzleMapper extends BaseMapper<Puzzle> {
}
