package faang.school.achievement.config;

import faang.school.achievement.messaging.publisher.AchievementPublisher;
import faang.school.achievement.messaging.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private final String host;
    @Value("${spring.data.redis.port}")
    private final int port;
    @Value("${spring.data.redis.channel.achievement}")
    private final String achievementChannel;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean
    public MessagePublisher achievementPublisher() {
        return new AchievementPublisher(redisTemplate(), achievementTopic());
    }

    @Bean
    public ChannelTopic achievementTopic() {
        return new ChannelTopic(achievementChannel);
    }
}
