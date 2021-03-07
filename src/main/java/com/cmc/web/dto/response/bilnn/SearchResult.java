package com.cmc.web.dto.response.bilnn;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chenmaochang
 * @create 2021/1/11 18:27
 */
@Data
public class SearchResult implements Serializable {
    private List<FileItem> objects;
    private Integer parent;
}
