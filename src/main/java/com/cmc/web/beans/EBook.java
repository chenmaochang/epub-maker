package com.cmc.web.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EBook implements Serializable {
    private String title;
    @Builder.Default
    private String language = "zh";
    private String identifier;
    private String creator;
    @Builder.Default
    private String publisher = "暂无";
    private String date;
}
