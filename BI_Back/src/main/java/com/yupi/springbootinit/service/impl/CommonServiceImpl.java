package com.yupi.springbootinit.service.impl;

import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.Evaluate;
import com.yupi.springbootinit.model.vo.ChartVo;
import com.yupi.springbootinit.model.vo.EvaluateVo;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.service.CommonService;
import com.yupi.springbootinit.service.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 公共服务类实现
 */
@Component
public class CommonServiceImpl implements CommonService {

    @Autowired
    private EvaluateService evaluateService;

    @Autowired
    private ChartService chartService;

    @Override
    public List<Evaluate> getEvaluateListByChartIdList(List<Long> chartIdList) {
        return evaluateService.getByChartIdList(chartIdList);
    }

    @Override
    public EvaluateVo getEvaluateVo(Evaluate evaluate) {
        return evaluateService.getEvaluateVo(evaluate);
    }

    @Override
    public List<Chart> getChartListByIdList(List<Long> chartIdList) {
        return chartService.listByIds(chartIdList);
    }

    @Override
    public ChartVo getChartVo(Chart chart) {
        return chartService.getChartVo(chart);
    }


}
