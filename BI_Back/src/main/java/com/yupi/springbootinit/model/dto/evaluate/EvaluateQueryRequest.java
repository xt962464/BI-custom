package com.yupi.springbootinit.model.dto.evaluate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 评价查询参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EvaluateQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 内容
     */
    private String content;


    /**
     * 图表id
     */
    private Long chartId;
    /**
     * 图表名称
     */
    private String chartName;
    /**
     * 用户名
     */
    private String userName;

    private String createTime;

}
