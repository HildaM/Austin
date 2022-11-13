package com.quan.austinhandler.deduplication.limit;

import cn.hutool.core.collection.CollUtil;
import com.quan.austincommon.constant.AustinConstant;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.deduplication.DeduplicationParam;
import com.quan.austinhandler.deduplication.service.AbstractDeduplicationService;
import com.quan.austinsupport.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 * date: 2022/11/13 下午 8:10
 *
 * @author Four
 */
@Service("SimpleLimitService")
public class SimpleLimitService extends AbstractLimitService {

    private static final String LIMIT_TAG = "SP_";

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Set<String> limitFilter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) {
        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceiver().size());

        // 存储redis数据
        Map<String, String> readyPutRedisReceiver = new HashMap<>(taskInfo.getReceiver().size());

        // 获取redis中现存的数据
        List<String> keys = deduplicationAllKey(service, taskInfo).stream()
                .map(key -> LIMIT_TAG + key)
                .collect(Collectors.toList());
        Map<String, String > inRedisValues = redisUtils.mGet(keys);


        for (String receiver : taskInfo.getReceiver()) {
            String key = LIMIT_TAG + deduplicationSingleKey(service, taskInfo, receiver);
            String inRedisValue = inRedisValues.get(key);

            if (inRedisValue != null && Integer.parseInt(inRedisValue) >= param.getCountNum()) {
                filterReceiver.add(receiver);
            } else {
                readyPutRedisReceiver.put(receiver,  key);     // 相当于更新对应的receiver
            }
        }

        // 不符合条件的用户：需要更新Redis(无记录添加，有记录则累加次数)
        putInRedis(readyPutRedisReceiver, inRedisValues, param.getDeduplicationTime());

        return filterReceiver;
    }

    // 存入redis做记录，用作去重标记
    private void putInRedis(Map<String, String> readyPutRedisReceiver, Map<String, String> inRedisValues, Long deduplicationTime) {
        Map<String, String> updateData = new HashMap<>(readyPutRedisReceiver.size());
        for (Map.Entry<String, String> entry : readyPutRedisReceiver.entrySet()) {
            String key = entry.getKey();
            if (inRedisValues.containsKey(key)) {
                updateData.put(key, String.valueOf(Integer.parseInt(inRedisValues.get(key)) + 1));
            } else {
                updateData.put(key, String.valueOf(AustinConstant.TRUE));
            }
        }

        if (CollUtil.isNotEmpty(updateData)) {
            redisUtils.pipelineSetEx(updateData, deduplicationTime);
        }
    }
}
