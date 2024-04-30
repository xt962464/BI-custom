package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.DeleteRequest;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.handler.AliYunGreenTextScanHandler;
import com.yupi.springbootinit.model.dto.comment.CommentAddRequest;
import com.yupi.springbootinit.model.dto.comment.CommentQueryRequest;
import com.yupi.springbootinit.model.dto.comment.CommentUpdateRequest;
import com.yupi.springbootinit.model.entity.Comment;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.CommentVo;
import com.yupi.springbootinit.service.CommentService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 留言控制器
 */
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Resource
    private UserService userService;

    @Autowired
    private AliYunGreenTextScanHandler aliYunGreenTextScanHandler;

    /**
     * 分页获取列表（封装类）
     *
     * @param commentQueryRequest 查询参数
     * @param request             http请求
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<CommentVo>> listPostVOByPage(@RequestBody CommentQueryRequest commentQueryRequest, HttpServletRequest request) {
        long current = commentQueryRequest.getCurrent();
        long size = commentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<Comment> postPage = commentService.page(new Page<>(current, size), commentService.getQueryWrapper(commentQueryRequest));
        Page<Comment> postPage = commentService.searchPage(commentQueryRequest);
        return ResultUtils.success(commentService.getCommentVOPage(postPage, request));
    }

    /**
     * 创建
     *
     * @param commentAddRequest
     * @param request
     */
    @PostMapping("/add")
    public BaseResponse<?> addPost(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 敏感词检测
        String checkResult = aliYunGreenTextScanHandler.checkText(commentAddRequest.getContent());
        if (checkResult == null || "408".equals(checkResult)) {
            Comment comment = new Comment();
            BeanUtils.copyProperties(commentAddRequest, comment);
            User loginUser = userService.getLoginUser(request);
            comment.setUserId(loginUser.getId());
            commentService.validPost(comment, true);
            boolean result = commentService.save(comment);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            long newCommentId = comment.getId();
            return ResultUtils.success(newCommentId);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR, checkResult);
    }


    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Comment comment = commentService.getById(id);
        ThrowUtils.throwIf(comment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!comment.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = commentService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param commentUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<?> updatePost(@RequestBody CommentUpdateRequest commentUpdateRequest) {
        if (commentUpdateRequest == null || commentUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 敏感词检测
        String checkResult = aliYunGreenTextScanHandler.checkText(commentUpdateRequest.getContent());
        if (checkResult == null || "408".equals(checkResult)) {
            Comment comment = new Comment();
            BeanUtils.copyProperties(commentUpdateRequest, comment);
            // 参数校验
            commentService.validPost(comment, false);
            long id = commentUpdateRequest.getId();
            // 判断是否存在
            Comment oldComment = commentService.getById(id);
            ThrowUtils.throwIf(oldComment == null, ErrorCode.NOT_FOUND_ERROR);
            boolean result = commentService.updateById(comment);
            return ResultUtils.success(result);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR, checkResult);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<CommentVo> getPostVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Comment comment = commentService.getById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(commentService.getCommentVo(comment, request));
    }


}
