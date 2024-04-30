package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.mapper.EvaluateMapper;
import com.yupi.springbootinit.model.dto.evaluate.EvaluateQueryRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.Evaluate;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.ChartVo;
import com.yupi.springbootinit.model.vo.EvaluateVo;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.EvaluateService;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评价服务实现类
 */
@Service
public class EvaluateServiceImpl extends ServiceImpl<EvaluateMapper, Evaluate> implements EvaluateService {

    @Autowired
    private UserService userService;

    @Autowired
    private ChartMapper chartMapper;


    @Override
    public Page<Evaluate> searchPage(EvaluateQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<Evaluate> page = new Page<>(current, size);
        Map<String, Object> params = new HashMap<>();

        // 图表搜索参数
        if (StringUtils.isNotBlank(queryRequest.getChartName())) {
            params.put("chartName", SqlUtils.generateLike(queryRequest.getChartName()));
        }
        // 用户搜索参数
        if (StringUtils.isNotBlank(queryRequest.getUserName())) {
            params.put("userName", SqlUtils.generateLike(queryRequest.getUserName()));
        }
        // 评价搜索参数
        if (StringUtils.isNotBlank(queryRequest.getContent())) {
            params.put("content", SqlUtils.generateLike(queryRequest.getContent()));
        }
        // 评价时间搜索参数
        if (StringUtils.isNotBlank(queryRequest.getCreateTime())) {
            params.put("createTime", queryRequest.getCreateTime());
        }
        List<Evaluate> dataList = this.baseMapper.searchPage(page, params);
        page.setRecords(dataList);
        return page;
    }

    @Override
    public QueryWrapper<Evaluate> getQueryWrapper(EvaluateQueryRequest evaluateQueryRequest) {
        QueryWrapper<Evaluate> queryWrapper = new QueryWrapper<>();
        if (evaluateQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = evaluateQueryRequest.getSortField();
        String sortOrder = evaluateQueryRequest.getSortOrder();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<EvaluateVo> getEvaluateVOPage(Page<Evaluate> pageData, HttpServletRequest request) {
        List<Evaluate> records = pageData.getRecords();
        Page<EvaluateVo> voPageData = new Page<>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        if (CollectionUtils.isEmpty(records)) {
            return voPageData;
        }
        // 1. 关联查询用户信息
        List<Long> userIdList = records.stream().map(Evaluate::getUserId).toList();
        List<Long> chartIdList = records.stream().map(Evaluate::getChartId).toList();
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdList).stream()
                .collect(Collectors.groupingBy(User::getId));

        Map<Long, List<Chart>> chartIdUserListMap = chartMapper.selectList(new QueryWrapper<>(new Chart()).in("id", chartIdList)).stream()
                .collect(Collectors.groupingBy(Chart::getId));

        // 填充信息
        List<EvaluateVo> voList = records.stream().map(data -> {
            EvaluateVo objToVo = EvaluateVo.objToVo(data);
            Long userId = objToVo.getUserId();
            Long chartId = objToVo.getChartId();
            User user = null;
            Chart chart = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
                objToVo.setUser(userService.getUserVO(user));
            }
            if (chartIdUserListMap.containsKey(chartId)) {
                chart = chartIdUserListMap.get(chartId).get(0);
                objToVo.setChart(ChartVo.objToVo(chart));
            }
            return objToVo;
        }).collect(Collectors.toList());
        voPageData.setRecords(voList);
        return voPageData;
    }

    @Override
    public void validPost(Evaluate evaluate, boolean add) {
        if (evaluate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long chartId = evaluate.getChartId();
        Long userId = evaluate.getUserId();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(chartId == null, ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (chartId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
    }

    @Override
    public EvaluateVo getEvaluateVo(Evaluate evaluate) {
        return getEvaluateVo(evaluate, null);
    }

    @Override
    public EvaluateVo getEvaluateVo(Evaluate evaluate, HttpServletRequest request) {
        EvaluateVo evaluateVo = EvaluateVo.objToVo(evaluate);
        // 1. 关联查询用户信息
        Long userId = evaluateVo.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        evaluateVo.setUser(userVO);
        return evaluateVo;
    }

    @Override
    public List<Evaluate> getByChartIdList(List<Long> chartIdList) {
        if (CollectionUtils.isEmpty(chartIdList)) {
            return new ArrayList<>();
        }
        return this.baseMapper.selectList(new QueryWrapper<>(new Evaluate()).in("chartId", chartIdList));
    }

}
