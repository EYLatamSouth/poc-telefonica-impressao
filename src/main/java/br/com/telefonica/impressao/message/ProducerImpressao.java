package br.com.telefonica.impressao.message;

import br.com.telefonica.impressao.model.ImpressaoMongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class ProducerImpressao {

    private static final Logger logger = LoggerFactory.getLogger(ProducerImpressao.class);

    @Value("${topic.name.producer}")
    private String topicName;

    private KafkaTemplate<String, ImpressaoMongoDB> kafkaTemplate = null;

    public void ImpressaoCobranca(@Value("${topic.name.producer}") String topic, KafkaTemplate<String, ImpressaoMongoDB> kafkaTemplate) {
        this.topicName = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ImpressaoMongoDB billing){
        kafkaTemplate.send(topicName, billing).addCallback(
                success -> logger.info("Messagem send: " + success.getProducerRecord().value()),
                failure -> logger.info("Message failure: " + failure.getMessage())
        );
    }
}
