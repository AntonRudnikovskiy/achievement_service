package faang.school.achievement.messaging.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class AchievementPublisher extends AbstractMessagePublisher implements MessagePublisher {
    @Autowired
    public AchievementPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        super(redisTemplate, topic);
    }

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
