package com.yupi.springbootinit.model.dto.comment;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentQueryRequest  extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;

    /**
     * 用户id
     */
    private Long userId;

    private String userName;

    private String createTime;

}
