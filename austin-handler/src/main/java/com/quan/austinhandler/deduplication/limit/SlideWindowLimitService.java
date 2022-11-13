package com.quan.austinhandler.deduplication.limit;

import cn.hutool.core.util.IdUtil;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.deduplication.DeduplicationParam;
import com.quan.austinhandler.deduplication.service.AbstractDeduplicationService;
import com.quan.austinsupport.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 * date: 2022/11/13 下午 7:22
 *
 * @author Four
 */
@Service("SlideWindowLimitService")
public class SlideWindowLimitService extends AbstractLimitService{

    private static final String LIMIT_TAG = "SW_";

    @Autowired
    private RedisUtils redisUtils;


    private DefaultRedisScript<Long> redisScript;


    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
    }


    /**
     * @param service  去重器对象
     * @param taskInfo
     * @param param    去重参数
     * @return 返回不符合条件的手机号码
     */
    @Override
    public Set<String> limitFilter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) {

        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceiver().size());
        long nowTime = System.currentTimeMillis();
        for (String receiver : taskInfo.getReceiver()) {
            // 生成唯一key
            String key = LIMIT_TAG + deduplicationSingleKey(service, taskInfo, receiver);
            // 使用雪花算法生成唯一value
            String scoreValue = String.valueOf(IdUtil.getSnowflake().nextId());
            // 获取当前时间字符串
            String score = String.valueOf(nowTime);

            // 采用redis lua脚本获取不符合条件的参数，装进filterReceiver中
            if (redisUtils.execLimitLua(
                    redisScript,
                    Arrays.asList(key),
                    String.valueOf(param.getDeduplicationTime() * 1000), score, String.valueOf(param.getCountNum()), scoreValue))
            {
                filterReceiver.add(receiver);
            }

        }
        return filterReceiver;
    }
}
