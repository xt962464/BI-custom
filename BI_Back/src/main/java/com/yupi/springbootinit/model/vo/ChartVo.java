package com.yupi.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yupi.springbootinit.model.entity.Chart;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ChartVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表数据
     */
    private String chartDate;

    /**
     * 图表类型
     */
    private String charType;

    /**
     * 图表所属用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;

    /**
     * 图表状态
     */
    private String status;

    /**
     * 生成图表执行信息
     */
    private String execMessage;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 评价
     */
    private List<EvaluateVo> evaluateList;

    /**
     * 当前用户是否评价
     */
    private Boolean isEvaluate;

    /**
     * 对象转包装类
     *
     * @param chart
     * @return
     */
    public static ChartVo objToVo(Chart chart) {
        if (chart == null) {
            return null;
        }
        ChartVo chartVo = new ChartVo();
        BeanUtils.copyProperties(chart, chartVo);
        return chartVo;
    }
}
