package com.yupi.springbootinit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.gson.Gson;
import com.yupi.springbootinit.model.entity.Evaluate;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class EvaluateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static Gson GSON = new Gson();

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 图表id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chartId;
    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String content;

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

    private UserVO user;

    private ChartVo chart;

    /**
     * 对象转包装类
     *
     * @param evaluate
     * @return
     */

    public static EvaluateVo objToVo(Evaluate evaluate) {
        if (evaluate == null) {
            return null;
        }
        EvaluateVo evaluateVo = new EvaluateVo();
        BeanUtils.copyProperties(evaluate, evaluateVo);
        return evaluateVo;
    }

}
