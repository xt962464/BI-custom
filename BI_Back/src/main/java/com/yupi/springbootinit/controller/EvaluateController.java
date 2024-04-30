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
import com.yupi.springbootinit.model.dto.evaluate.EvaluateAddRequest;
import com.yupi.springbootinit.model.dto.evaluate.EvaluateQueryRequest;
import com.yupi.springbootinit.model.dto.evaluate.EvaluateUpdateRequest;
import com.yupi.springbootinit.model.entity.Evaluate;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.EvaluateVo;
import com.yupi.springbootinit.service.EvaluateService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/evaluate")
@Slf4j
public class EvaluateController {

    @Autowired
    private EvaluateService evaluateService;

    @Resource
    private UserService userService;

    @Autowired
    private AliYunGreenTextScanHandler aliYunGreenTextScanHandler;

    /**
     * 分页获取列表（封装类）
     *
     * @param evaluateQueryRequest 查询参数
     * @param request              http请求
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EvaluateVo>> listPostVOByPage(@RequestBody EvaluateQueryRequest evaluateQueryRequest, HttpServletRequest request) {
        long current = evaluateQueryRequest.getCurrent();
        long size = evaluateQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Evaluate> postPage = evaluateService.searchPage(evaluateQueryRequest);
        return ResultUtils.success(evaluateService.getEvaluateVOPage(postPage, request));
    }

    /**
     * 添加评价
     *
     * @param evaluateAddRequest
     * @param request
     */
    @PostMapping("/add")
    public BaseResponse<?> addPost(@RequestBody EvaluateAddRequest evaluateAddRequest, HttpServletRequest request) {
        if (evaluateAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 敏感词检测
        String checkResult = aliYunGreenTextScanHandler.checkText(evaluateAddRequest.getContent());
        if (checkResult == null || "408".equals(checkResult)) {
            // 识别结果为空,则表示无敏感词
            Evaluate evaluate = new Evaluate();
            BeanUtils.copyProperties(evaluateAddRequest, evaluate);
            User loginUser = userService.getLoginUser(request);
            evaluate.setUserId(loginUser.getId());
            evaluateService.validPost(evaluate, true);
            boolean result = evaluateService.save(evaluate);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            long newEvaluateId = evaluate.getId();
            return ResultUtils.success(newEvaluateId);
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
        Evaluate evaluate = evaluateService.getById(id);
        ThrowUtils.throwIf(evaluate == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!evaluate.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = evaluateService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param evaluateUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<?> updatePost(@RequestBody EvaluateUpdateRequest evaluateUpdateRequest) {
        if (evaluateUpdateRequest == null || evaluateUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 敏感词检测
        String checkResult = aliYunGreenTextScanHandler.checkText(evaluateUpdateRequest.getContent());
        if (checkResult == null || "408".equals(checkResult)) {
            Evaluate evaluate = new Evaluate();
            BeanUtils.copyProperties(evaluateUpdateRequest, evaluate);
            // 参数校验
            evaluateService.validPost(evaluate, false);
            long id = evaluateUpdateRequest.getId();
            // 判断是否存在
            Evaluate oldEvaluate = evaluateService.getById(id);
            ThrowUtils.throwIf(oldEvaluate == null, ErrorCode.NOT_FOUND_ERROR);
            boolean result = evaluateService.updateById(evaluate);
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
    public BaseResponse<EvaluateVo> getPostVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Evaluate evaluate = evaluateService.getById(id);
        if (evaluate == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(evaluateService.getEvaluateVo(evaluate, request));
    }


}
