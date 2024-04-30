package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.dto.evaluate.EvaluateQueryRequest;
import com.yupi.springbootinit.model.entity.Evaluate;
import com.yupi.springbootinit.model.vo.EvaluateVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评价服务
 */
public interface EvaluateService extends IService<Evaluate> {

    /**
     * 搜索
     *
     * @param queryRequest 参数
     */
    Page<Evaluate> searchPage(EvaluateQueryRequest queryRequest);

    /**
     * 获取查询对象
     * @param evaluateQueryRequest
     * @return
     */
    QueryWrapper<Evaluate> getQueryWrapper(EvaluateQueryRequest evaluateQueryRequest);

    /**
     * 类型转换
     * @param postPage
     * @param request
     * @return
     */
    Page<EvaluateVo> getEvaluateVOPage(Page<Evaluate> postPage, HttpServletRequest request);

    void validPost(Evaluate evaluate, boolean add);

    EvaluateVo getEvaluateVo(Evaluate evaluate);

    EvaluateVo getEvaluateVo(Evaluate evaluate, HttpServletRequest request);

    List<Evaluate> getByChartIdList(List<Long> chartIdList);


}
