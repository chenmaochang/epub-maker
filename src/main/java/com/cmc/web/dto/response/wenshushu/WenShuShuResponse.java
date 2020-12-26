package com.cmc.web.dto.response.wenshushu;


import lombok.Data;

import java.io.Serializable;

@Data
public class WenShuShuResponse<T> implements Serializable {
    private Integer code;
    private T data;
    private String message;
}
