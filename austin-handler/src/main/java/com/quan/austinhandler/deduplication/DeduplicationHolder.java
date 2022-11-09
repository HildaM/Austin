package com.quan.austinhandler.deduplication;

import com.quan.austinhandler.deduplication.build.Builder;
import com.quan.austinhandler.deduplication.service.DeduplicationService;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * date: 2022/11/09 下午 8:07
 *
 * @author Four
 */
@Service
public class DeduplicationHolder {
    private final Map<Integer, Builder> builderHolder = new HashMap<>(4);
    private final Map<Integer, DeduplicationService> serviceHolder = new HashMap<>(4);

    public Builder getBuilderByKey(Integer key) {
        return builderHolder.get(key);
    }

    public DeduplicationService getServiceByKey(Integer key) {
        return serviceHolder.get(key);
    }

    public void putBuilder(Integer key, Builder builder) {
        builderHolder.put(key, builder);
    }

    public void putService(Integer key, DeduplicationService service) {
        serviceHolder.put(key, service);
    }
}
