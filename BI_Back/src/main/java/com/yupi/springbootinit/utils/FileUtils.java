package com.yupi.springbootinit.utils;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileUtils {

    public final static String FILE_UPLOAD_PATH = "W:\\Workspace\\BI-custom\\BI_Back\\src\\main\\webapp\\static\\upload\\";

    /**
     * 保存文件
     *
     * @param file 文件
     */
    public static String upload(MultipartFile file) {
        if (file.getSize() > 1024 * 1024 * 10) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件不能超过10M");
        }
        //获取文件后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());
        if (!"jpg,jpeg,gif,png".toUpperCase().contains(suffix.toUpperCase())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "请选择jpg,jpeg,gif,png格式的图片");
        }
        String savePath = FILE_UPLOAD_PATH;
        File savePathFile = new File(savePath);
        if (!savePathFile.exists()) {
            //若不存在该目录，则创建目录
            savePathFile.mkdir();
        }
        //通过UUID生成唯一文件名
        String filename = System.currentTimeMillis() + "." + suffix;
        try {
            //将文件保存指定目录
            file.transferTo(new File(savePath + filename));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存文件异常");
        }
        //返回文件名称
        return String.format("/img/%s", filename);
    }


}
