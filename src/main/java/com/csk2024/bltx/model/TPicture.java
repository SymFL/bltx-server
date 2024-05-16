package com.csk2024.bltx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 图片表
 * t_picture
 */
@Data
public class TPicture implements Serializable {
    /**
     * 主键，自动增长，图片ID
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
     * 图片URL
     */
    private String url;

    /**
     * 备注
     */
    private String remark;

    /**
     * 预测结果
     */
    private String result;

    /**
     * 预测可能性
     */
    private BigDecimal probability;

    /**
     * 用户姓名
     */
    private String userName;

    private static final long serialVersionUID = 1L;
}