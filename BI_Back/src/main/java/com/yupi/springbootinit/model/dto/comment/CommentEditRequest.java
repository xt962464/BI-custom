package com.yupi.springbootinit.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String content;

    /**
     * 用户id
     */
    private Long userId;

}
