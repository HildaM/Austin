package com.quan.austinhandler.shield.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.quan.austincommon.domain.AnchorInfo;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.AnchorState;
import com.quan.austincommon.enums.ShieldType;
import com.quan.austinhandler.shield.ShieldService;
import com.quan.austinsupport.utils.LogUtils;
import com.quan.austinsupport.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;

/**
 * Description:
 * date: 2022/11/22 下午 4:04
 *
 * @author Four
 */

@Service
public class ShieldServiceImpl implements ShieldService {

    private static final String NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY = "night_shield_send";
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private LogUtils logUtils;

    @Override
    public void shield(TaskInfo taskInfo) {
        if (ShieldType.NIGHT_NO_SHIELD.getCode().equals(taskInfo.getShieldType())) {
            return;
        }

        // 夜间屏蔽，不发送
        if (isNight()) {
            // 夜间屏蔽
            if (ShieldType.NIGHT_SHIELD.getCode().equals(taskInfo.getShieldType())) {
                logUtils.print(
                        AnchorInfo.builder()
                                .state(AnchorState.NIGHT_SHIELD.getCode())
                                .businessId(taskInfo.getBusinessId())
                                .ids(taskInfo.getReceiver())
                                .build()
                );
            }

            // 夜间屏蔽，但是明天发送
            if (ShieldType.NIGHT_SHIELD_BUT_NEXT_DAY_SEND.getCode().equals(taskInfo.getShieldType())) {
                redisUtils.lPush(
                        NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY,
                        JSON.toJSONString(taskInfo, SerializerFeature.WriteClassName),
                        (DateUtil.offsetDay(new Date(), 1).getTime() / 1000) - DateUtil.currentSeconds()
                );
                logUtils.print(AnchorInfo.builder().state(AnchorState.NIGHT_SHIELD_NEXT_SEND.getCode()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());
            }

            // 将接收者置空，表示取消发送
            taskInfo.setReceiver(new HashSet<>());
        }
    }

    // 在早上8点前一律认为是凌晨
    private boolean isNight() {
        return LocalDateTime.now().getHour() < 8;
    }
}
