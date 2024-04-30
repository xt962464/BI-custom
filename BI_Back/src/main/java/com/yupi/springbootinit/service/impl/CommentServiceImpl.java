package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.mapper.CommentMapper;
import com.yupi.springbootinit.model.dto.comment.CommentQueryRequest;
import com.yupi.springbootinit.model.entity.Comment;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.CommentVo;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.CommentService;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评价服务实现类
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Override
    public Page<Comment> searchPage(CommentQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<Comment> page = new Page<>(current, size);
        Map<String, Object> params = new HashMap<>();

        // 用户搜索参数
        if (StringUtils.isNotBlank(queryRequest.getUserName())) {
            params.put("userName", SqlUtils.generateLike(queryRequest.getUserName()));
        }
        // 留言搜索参数
        if (StringUtils.isNotBlank(queryRequest.getContent())) {
            params.put("content", SqlUtils.generateLike(queryRequest.getContent()));
        }
        // 时间搜索参数
        if (StringUtils.isNotBlank(queryRequest.getCreateTime())) {
            params.put("createTime", queryRequest.getCreateTime());
        }
        List<Comment> dataList = this.baseMapper.searchPage(page, params);
        page.setRecords(dataList);
        return page;
    }

    @Override
    public QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (commentQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = commentQueryRequest.getSortField();
        String sortOrder = commentQueryRequest.getSortOrder();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<CommentVo> getCommentVOPage(Page<Comment> pageData, HttpServletRequest request) {
        List<Comment> records = pageData.getRecords();
        Page<CommentVo> voPageData = new Page<>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        if (CollectionUtils.isEmpty(records)) {
            return voPageData;
        }
        // 1. 关联查询用户信息
        List<Long> userIdList = records.stream().map(Comment::getUserId).toList();
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdList).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<CommentVo> voList = records.stream().map(data -> {
            CommentVo objToVo = CommentVo.objToVo(data);
            Long userId = objToVo.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            objToVo.setUser(userService.getUserVO(user));
            return objToVo;
        }).collect(Collectors.toList());
        voPageData.setRecords(voList);
        return voPageData;
    }

    @Override
    public void validPost(Comment comment, boolean add) {
        if (comment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = comment.getUserId();
        String content = comment.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(userId == null || StringUtils.isBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isBlank(content) || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
    }

    @Override
    public CommentVo getCommentVo(Comment comment) {
        return getCommentVo(comment, null);
    }

    public CommentVo getCommentVo(Comment comment, HttpServletRequest request) {
        CommentVo commentVo = CommentVo.objToVo(comment);
        // 1. 关联查询用户信息
        Long userId = commentVo.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        commentVo.setUser(userVO);
        return commentVo;
    }
}
