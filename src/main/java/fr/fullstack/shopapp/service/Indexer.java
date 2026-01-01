package fr.fullstack.shopapp.service;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class Indexer implements ApplicationListener<ApplicationReadyEvent> {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Cette méthode est appelée automatiquement quand l'application a fini de démarrer.
     * Elle lance l'indexation massive des données SQL vers Elasticsearch.
     */
    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("Début de l'indexation Elasticsearch (E_TEC_90)...");

        try {
            MassIndexer massIndexer = Search.session(entityManager).massIndexer();
            
            // On lance l'indexation de manière asynchrone pour ne pas bloquer le thread principal trop longtemps,
            // mais startAndWait() est souvent préférable en dev pour être sûr que c'est fini avant de tester.
            massIndexer.startAndWait();
            
            System.out.println("Indexation terminée avec succès !");
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de l'indexation : " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}