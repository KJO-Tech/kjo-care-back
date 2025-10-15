package kjo.care.msvc_notification.config;

import kjo.care.msvc_notification.dto.ReactionEventDto;
import kjo.care.msvc_notification.utils.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, NotificationEvent<?>> consumerFactory() {
        var configs = new HashMap<String,Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getBootstrapServers());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<NotificationEvent<?>> deserializer = new JsonDeserializer<>(NotificationEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,NotificationEvent<?>> validMessageContainerFactory(ConsumerFactory<String, NotificationEvent<?>> consumerFactory){
        var factory = new ConcurrentKafkaListenerContainerFactory<String,NotificationEvent<?>>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
