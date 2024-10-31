package com.insurance.insuranceQuote.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:9094", "port=9094" },
topics = {"${producer.topic.name}" })
public class KafkaProducerServiceIntegrationTest {

    @Value(value = "${producer.topic.name}")
    private String producerTopic;

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Test
    public void shouldProduceAndConsumeKafkaTopic() {

        String exampleMsg = "{\"id\":1,\"insurance_policy_id\":null,\"product_id\":\"1b2da7cc-b367-4196-8a78-9cfeec21f587\",\"offer_id\":\"adc56d77-348c-4bf0-908f-22d402ee715c\",\"category\":\"HOME\",\"created_at\":[2024,10,30,10,40,9,265176419],\"update_at\":null,\"total_monthly_premium_amount\":75.25,\"assistances\":[\"Encanador\",\"Eletricista\",\"Chaveiro 24h\"],\"customer\":{\"document_number\":\"36205578900\",\"name\":\"John Wick\",\"type\":\"NATURAL\",\"gender\":\"MALE\",\"date_of_birth\":\"1973-05-02\",\"email\":\"johnwick@gmail.com\",\"phone_number\":\"11950503030\"},\"coverages\":{\"Incêndio\":250000.0,\"Desastres naturais\":500000.0,\"Responsabiliadade civil\":75000.0}}";
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("consumer_grup", "false", broker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory(
                consumerProps, new StringDeserializer(), new StringDeserializer()
        );
        Consumer<String, String> consumerServiceTest = consumerFactory.createConsumer();

        broker.consumeFromAnEmbeddedTopic(consumerServiceTest, producerTopic);

        kafkaProducerService.sendMessage(exampleMsg);

        ConsumerRecord<String, String> consumerRecordOfExampleDTO = KafkaTestUtils.getSingleRecord(consumerServiceTest, producerTopic);
        String valueReceived = consumerRecordOfExampleDTO.value();

        assertThat(valueReceived).contains("\"product_id\":\"1b2da7cc-b367-4196-8a78-9cfeec21f587\"");

        consumerServiceTest.close();
    }
}
