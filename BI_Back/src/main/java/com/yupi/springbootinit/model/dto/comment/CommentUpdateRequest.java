package com.yupi.springbootinit.model.dto.comment;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String content;

}
