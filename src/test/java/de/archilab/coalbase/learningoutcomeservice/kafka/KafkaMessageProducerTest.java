package de.archilab.coalbase.learningoutcomeservice.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcome;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcomeDomainEvent;
import de.archilab.coalbase.learningoutcomeservice.learningoutcome.LearningOutcomeEventType;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"test-topic"})
public class KafkaMessageProducerTest {

  private static final String TOPIC = "test-topic";

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Test
  public void sendEvent() throws JsonProcessingException, InterruptedException {
    Map<String, Object> consumerProps = KafkaTestUtils
        .consumerProps("testT", "false", this.embeddedKafka);

    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(
        consumerProps);

    ContainerProperties containerProperties = new ContainerProperties(
        KafkaMessageProducerTest.TOPIC);

    KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(
        cf, containerProperties);

    final BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
    container.setupMessageListener(new MessageListener<String, String>() {

      @Override
      public void onMessage(ConsumerRecord<String, String> record) {
        records.add(record);
      }
    });
    container.setBeanName("templateTests");
    container.start();
    ContainerTestUtils.waitForAssignment(container, this.embeddedKafka.getPartitionsPerTopic());
    Map<String, Object> senderProps =
        KafkaTestUtils.senderProps(this.embeddedKafka.getBrokersAsString());
    senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(senderProps);
    KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);

    ObjectMapper objectMapper = new ObjectMapper();

    KafkaMessageProducer learningOutcomeMessageProducer = new KafkaMessageProducer(template,
        objectMapper);

    UniqueId<LearningOutcome> uniqueId = new UniqueId<>();

    LearningOutcomeDomainEvent learningOutcomeDomainEvent = new LearningOutcomeDomainEvent(uniqueId,
        LearningOutcomeEventType.CREATED);

    learningOutcomeMessageProducer.send(KafkaMessageProducerTest.TOPIC, learningOutcomeDomainEvent);

    ConsumerRecord<String, String> record = records.poll(10, TimeUnit.SECONDS);

    assertThat(record.key()).isEqualTo(learningOutcomeDomainEvent.getEventID().toString());

    assertThat(record.value())
        .isEqualTo(objectMapper.writeValueAsString(learningOutcomeDomainEvent));
  }

}
