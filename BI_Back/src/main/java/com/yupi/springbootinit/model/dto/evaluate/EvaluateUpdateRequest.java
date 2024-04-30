package com.yupi.springbootinit.model.dto.evaluate;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class EvaluateUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String content;


    /**
     * 图表id
     */
    private Long chartId;

}
