package com.cgy.seckill.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

//    public static final String QUEUE_NAME = "queue";
//    public static final String QUEUE_NAME1 = "queue1";
//    public static final String QUEUE_NAME2 = "queue2";
//    public static final String TOPIC_EXCHANGE = "topicExchange";
//    public static final String ROUTING_KEY1 = "topic.key1";
//    public static final String ROUTING_KEY2 = "topic.key2";
//    public static final String ROUTING_KEY_ALL = "topic.#"; // '#' matches 1 or more words, '*' matches 1 word
//    public static final String FANOUT_EXCHANGE = "fanoutExchange";
//    public static final String HEADERS_EXCHANGE = "headersExchange";
//    public static final String HEADERS_QUEUE_NAME = "headers.queue";
//
//    // Direct mode
//    @Bean
//    public Queue queue() {
//        return new Queue(QUEUE_NAME, true);
//    }
//
//    // Topic mode
//    @Bean
//    public Queue queue1() {
//        return new Queue(QUEUE_NAME1, true);
//    }
//    @Bean
//    public Queue queue2() {
//        return new Queue(QUEUE_NAME2, true);
//    }
//    @Bean
//    public TopicExchange topicExchange() {
//        return new TopicExchange(TOPIC_EXCHANGE);
//    }
//    @Bean
//    public Binding topicBinding1() {
//        return BindingBuilder.bind(queue1()).to(topicExchange()).with(ROUTING_KEY1);
//    }
//    @Bean
//    public Binding topicBinding2() {
//        return BindingBuilder.bind(queue2()).to(topicExchange()).with(ROUTING_KEY_ALL);
//    }
//
//    // Fanout mode
//    @Bean
//    public FanoutExchange fanoutExchange() {
//        return new FanoutExchange(FANOUT_EXCHANGE);
//    }
//    @Bean
//    public Binding fanoutBinding1() {
//        return BindingBuilder.bind(queue1()).to(fanoutExchange());
//    }
//    @Bean
//    public Binding fanoutBinding2() {
//        return BindingBuilder.bind(queue2()).to(fanoutExchange());
//    }
//
//    // Headers mode
//    @Bean
//    public HeadersExchange headersExchange() {
//        return new HeadersExchange(HEADERS_EXCHANGE);
//    }
//    @Bean
//    public Queue headersQueue() {
//        return new Queue(HEADERS_QUEUE_NAME, true);
//    }
//    @Bean
//    public Binding headersBinding() {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("header1", "value1");
//        map.put("header2", "value2");
//        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
//    }

    public static final String SECKILL_QUEUE_NAME = "seckill.queue";

    @Bean
    public Queue seckillQueue() {
        return new Queue(SECKILL_QUEUE_NAME, true);
    }
}
