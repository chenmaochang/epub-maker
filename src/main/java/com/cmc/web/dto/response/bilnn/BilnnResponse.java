package com.cmc.web.dto.response.bilnn;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenmaochang
 * @create 2021/1/11 18:17
 */
@Data
public class BilnnResponse<T> implements Serializable {
    private Integer code;
    private T data;
    private String msg;
}
