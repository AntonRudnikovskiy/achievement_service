package faang.school.achievement.messaging.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public abstract class AbstractMessagePublisher {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ChannelTopic topic;
}
