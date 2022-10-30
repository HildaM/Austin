package com.quan.austinhandler.utils;

import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.ChannelType;
import com.quan.austincommon.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

/*
    GroupId工具类
 */
public class GroupIdMappingUtils {

    /*
        获取所有GroupId
        对所有渠道Channel和消息类型MessageType，取笛卡尔积
     */
    public static List<String> getAllGroupIds() {
        List<String> groupIds = new ArrayList<>();

        for (ChannelType channel : ChannelType.values()) {
            for (MessageType message : MessageType.values()) {
                groupIds.add(channel.getCodeEn() + "." + message.getCodeEn());
            }
        }

        return groupIds;
    }

    /*
        根据TaskInfo获取当前消息的GroupId
     */
    public static String getGroupIdByTaskInfo(TaskInfo taskInfo) {
        String channelCodeEn = ChannelType.getEnumByCode(taskInfo.getSendChannel()).getCodeEn();
        String msgCodeEn = MessageType.getEnumByCode(taskInfo.getMsgType()).getCodeEn();
        return channelCodeEn + "." + msgCodeEn;
    }
}
