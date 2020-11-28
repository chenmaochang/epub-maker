package com.cmc.web.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjaxResult<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
}
