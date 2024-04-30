package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.Evaluate;
import com.yupi.springbootinit.model.vo.ChartVo;
import com.yupi.springbootinit.model.vo.EvaluateVo;

import java.util.Arrays;
import java.util.List;

public interface CommonService {
    List<Evaluate> getEvaluateListByChartIdList(List<Long> chartIdList);

    EvaluateVo getEvaluateVo(Evaluate evaluate);

    List<Chart> getChartListByIdList(List<Long> chartIdList);

    ChartVo getChartVo(Chart chart);
}
