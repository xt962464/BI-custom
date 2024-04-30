package com.yupi.springbootinit.model.dto.evaluate;

import lombok.Data;

import java.io.Serializable;

@Data
public class EvaluateAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;


    /**
     * 图表id
     */
    private Long chartId;

}
