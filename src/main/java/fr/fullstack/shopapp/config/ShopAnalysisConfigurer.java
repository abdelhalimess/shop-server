package fr.fullstack.shopapp.config;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;
import org.springframework.stereotype.Component;

@Component("shopAnalysisConfigurer")
public class ShopAnalysisConfigurer implements ElasticsearchAnalysisConfigurer {

    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
        // Définition de l'analyseur "name" utilisé dans vos entités
        context.analyzer("name").custom()
                .tokenizer("standard")
                .tokenFilters("lowercase", "asciifolding"); // asciifolding enlève les accents (été -> ete)
        
        // L'analyseur "english" est déjà inclus dans Elasticsearch, pas besoin de le redéfinir
    }
}