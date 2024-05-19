package com.csk2024.bltx.query;

import jakarta.security.auth.message.callback.PrivateKeyCallback;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PictureQuery extends BaseQuery{
    private Integer current;

    /**
     * 图片Id
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 图片
     */
    private MultipartFile picture;

    /**
     * 图片URL
     */
    private String url;

    /**
     * 图片的base64字符串
     */
    private String encodedString;
}
