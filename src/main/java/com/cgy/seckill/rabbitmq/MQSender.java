package com.cgy.seckill.rabbitmq;

import com.cgy.seckill.utils.StringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

//    public void send(Object message) {
//        String msg = StringBeanUtil.beanToString(message);
//        log.info("Send message: " + msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME, msg);
//    }
//
//    public void sendTopic(Object message) {
//        String msg = StringBeanUtil.beanToString(message);
//        log.info("Send topic message: " + msg);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY1, msg + "1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY2, msg + "2");
//    }
//
//    public void sendFanout(Object message) {
//        String msg = StringBeanUtil.beanToString(message);
//        log.info("Send fanout message: " + msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
//    }
//
//    public void sendHeaders(Object message) {
//        String msg = StringBeanUtil.beanToString(message);
//        log.info("Send headers message: " + msg);
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setHeader("header1", "value1");
//        messageProperties.setHeader("header2", "value2");
//        Message m = new Message(msg.getBytes(), messageProperties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", m);
//    }

    public void sendSeckillMessage(SeckillMessage sm) {
        String msg = StringBeanUtil.beanToString(sm);
        log.info("Send seckill message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE_NAME, msg);
    }
}
