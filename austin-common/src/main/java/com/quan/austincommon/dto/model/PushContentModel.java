package com.quan.austincommon.dto.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 3y
 *
 * 通知栏消息推送
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushContentModel extends ContentModel {

    private String title;
    private String content;
    private String url;
}