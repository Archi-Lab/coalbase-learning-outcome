package de.archilab.coalbase.learningoutcomeservice.learningoutcome;

import static org.junit.Assert.assertEquals;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
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

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.archilab.coalbase.learningoutcomeservice.core.DomainEvent;
import de.archilab.coalbase.learningoutcomeservice.core.EventType;
import de.archilab.coalbase.learningoutcomeservice.core.UniqueId;


@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1,
    topics = {"test-topic"})
public class LearningOutcomeMessageProducerTest {


  private static final String TOPIC = "test-topic";

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Test
  public void sendEvent() throws JsonProcessingException, InterruptedException {
    Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testT", "false",
        embeddedKafka);
    DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(
        consumerProps);

    ContainerProperties containerProperties = new ContainerProperties(TOPIC);

    KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(
        cf, containerProperties);

    final BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
    container.setupMessageListener(new MessageListener<String, String>() {

      @Override
      public void onMessage(ConsumerRecord<String, String> record) {
        System.out.println(record);
        records.add(record);
      }

    });
    container.setBeanName("templateTests");
    container.start();
    ContainerTestUtils
        .waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    Map<String, Object> senderProps =
        KafkaTestUtils.senderProps(embeddedKafka.getBrokersAsString());
    senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(senderProps);
    KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);

    LearningOutcomeMessageProducer learningOutcomeMessageProducer = new LearningOutcomeMessageProducer(
        TOPIC, template, new ObjectMapper());

    Competence competence = new Competence(
        "Die Studierenden können Marketingentscheidungen informationsgestützt treffen",
        TaxonomyLevel.SYNTHESIS);

    Tool tool0 = new Tool(
        "das Makro- und Mikroumfeld des relevanten Marktes so wie das eigenen Unternehmen analysieren");
    Tool tool1 = new Tool(
        "Konsequenzen für die verschiedenen Bereiche der Marketingpolitik entwerfen");
    Purpose purpose = new Purpose(
        "Produkte, Preise, Kommunikation und den Vertrieb bewusst marktorientiert zu gestalten");


    LearningOutcome learningOutcome = new LearningOutcome(competence,
        Arrays.asList(tool0, tool1), purpose);


    LearningOutcomeDomainEvent learningOutcomeDomainEvent = new LearningOutcomeDomainEvent();


    learningOutcomeMessageProducer.send(learningOutcomeDomainEvent);

    assertEquals(records.poll(10, TimeUnit.SECONDS).key(),
        learningOutcomeDomainEvent.getEventID().toString());

  }

}
