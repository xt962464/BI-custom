package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.Evaluate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EvaluateMapper extends BaseMapper<Evaluate> {

    /**
     * 分页搜索
     *
     * @param page   分页对象
     * @param params 参数
     */
    List<Evaluate> searchPage(Page<Evaluate> page, @Param("params") Map<String, Object> params);
}
