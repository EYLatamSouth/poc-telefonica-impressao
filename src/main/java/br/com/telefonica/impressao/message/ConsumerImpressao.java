package br.com.telefonica.impressao.message;

import br.com.telefonica.impressao.model.ImpressaoMongoDB;
import br.com.telefonica.impressao.repository.ImpressaoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

public class ConsumerImpressao {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerImpressao.class);

    @Autowired
    private ImpressaoRepository impressaoRepository;

    @Value("${topic.name.consumer}")
    private String topicName;

    @KafkaListener(topics = "${topic.name.consumer}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> payload){
        logger.info("Tópico: {}", topicName);
        logger.info("key: {}", payload.key());
        logger.info("Headers: {}", payload.headers());
        logger.info("Partion: {}", payload.partition());
        logger.info("Order: {}", payload.value());

        String dados = payload.value();

        ObjectMapper mapper = new ObjectMapper();
        ImpressaoMongoDB billing;

        try {
            billing = mapper.readValue(dados, ImpressaoMongoDB.class);
        } catch (JsonProcessingException ex) {
            logger.error("Falha ao converter os dados recebidos pelo Consumer [dados={}]", dados, ex);
            return;
        }

        logger.info("Evento Recebido = {}", billing);


        ImpressaoMongoDB billingSaved = impressaoRepository.save(billing);
    }
}
