package com.quan.austincommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * date: 2022/11/09 下午 8:10
 *
 * @author Four
 */
@Getter
@ToString
@AllArgsConstructor
public enum DeduplicationType {
    CONTENT(10, "N分钟相同内容去重"),
    FREQUENCY(20, "一天内N次相同渠道去重"),
    ;

    private Integer code;
    private String description;

    /*
     * @author
     * @createTime 2022/11/09 下午 8:13
     * @desc 获取渠道
     */
    public static List<Integer> getDeduplicationList() {
        List<Integer> res = new ArrayList<>();
        for (DeduplicationType value : DeduplicationType.values()) {
            res.add(value.getCode());
        }

        return res;
    }
}
