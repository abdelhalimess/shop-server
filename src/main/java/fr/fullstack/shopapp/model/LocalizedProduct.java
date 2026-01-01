package fr.fullstack.shopapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

@Getter
@Setter
@Entity
@Table(name = "localized_product")
public class LocalizedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Locale locale;

    // Indexation pour la recherche textuelle (Nom)
    @FullTextField(analyzer = "name")
    private String name;

    // Indexation pour la recherche textuelle (Description)
    @FullTextField(analyzer = "english")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
}