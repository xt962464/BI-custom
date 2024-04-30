package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.dto.comment.CommentQueryRequest;
import com.yupi.springbootinit.model.entity.Comment;
import com.yupi.springbootinit.model.vo.CommentVo;

import javax.servlet.http.HttpServletRequest;

/**
 * 评价服务
 */
public interface CommentService extends IService<Comment> {

    /**
     * 分页查询
     *
     * @param commentQueryRequest 参数
     */
    Page<Comment> searchPage(CommentQueryRequest commentQueryRequest);

    QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest eommentQueryRequest);

    Page<CommentVo> getCommentVOPage(Page<Comment> postPage, HttpServletRequest request);

    void validPost(Comment eomment, boolean add);

    CommentVo getCommentVo(Comment eomment);

    CommentVo getCommentVo(Comment eomment, HttpServletRequest request);


}
