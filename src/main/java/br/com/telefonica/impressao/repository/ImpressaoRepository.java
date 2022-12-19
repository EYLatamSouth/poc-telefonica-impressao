package br.com.telefonica.impressao.repository;

import br.com.telefonica.impressao.model.ImpressaoMongoDB;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImpressaoRepository extends MongoRepository<ImpressaoMongoDB, Integer> {

    List<ImpressaoMongoDB> findByBillingStatus(String status);

    ImpressaoMongoDB findByBilling_id(Long id);
}

