package com.yupi.springbootinit.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.green20220302.models.TextModerationResponse;
import com.aliyun.green20220302.models.TextModerationResponseBody;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文本审核增强版
 */
@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class AliYunGreenTextScanHandler {

    private String accessKeyId;
    private String secret;


    /**
     * 使用AK&SK初始化账号Client
     *
     * @return Client
     * @throws Exception
     */
    public com.aliyun.green20220302.Client createClient() throws Exception {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(secret);
        // Endpoint 请参考 https://api.aliyun.com/product/Green
        config.endpoint = "green-cip.cn-shanghai.aliyuncs.com";
        return new com.aliyun.green20220302.Client(config);
    }


    public String checkText(String content) {
        try {
            com.aliyun.green20220302.Client client = this.createClient();
            com.aliyun.green20220302.models.TextModerationRequest textModerationRequest = new com.aliyun.green20220302.models.TextModerationRequest()
                    .setService("comment_detection")
                    .setServiceParameters(String.format("{\"content\":\"%s\"}", content));
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            // 复制代码运行请自行打印 API 的返回值
            TextModerationResponse textModerationResponse = client.textModerationWithOptions(textModerationRequest, runtime);
            TextModerationResponseBody body = textModerationResponse.getBody();
            Integer code = body.getCode();
            if (code == 200) {
                TextModerationResponseBody.TextModerationResponseBodyData responseBodyData = body.getData();
                String reason = responseBodyData.getReason();
                if (StringUtils.isNotBlank(reason)) {
                    // 含有敏感词
                    log.warn("内容:[{}]含有敏感词[{}]", content, reason);
                    JSONObject resultJson = JSON.parseObject(reason);
                    return resultJson.getString("riskTips");
                }
            } else if (code == 408) {
                // 原因：可能是您的账号未授权、账号欠费、账号未开通、账号被禁等。
                return "408";
            }
        } catch (Exception e) {
            log.error("[{}]内容敏感词识别发生错误", content, e);
        }
        return null;
    }

}
