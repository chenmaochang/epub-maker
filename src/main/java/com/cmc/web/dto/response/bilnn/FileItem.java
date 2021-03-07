package com.cmc.web.dto.response.bilnn;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenmaochang
 * @create 2021/1/11 18:25
 */
@Data
public class FileItem implements Serializable {
    private String id;
    private String name;
    private String path;
    private String pic;
    private Long size;
    private String type;
    private String date;
}
