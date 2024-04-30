package com.yupi.springbootinit.model.dto.evaluate;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class EvaluateEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;


    /**
     * 图表id
     */
    private Long chartId;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 内容
     */
    private String content;

}
