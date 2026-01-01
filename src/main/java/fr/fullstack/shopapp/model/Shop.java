package fr.fullstack.shopapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shops")
@Indexed
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @FullTextField(analyzer = "name")
    private String name;

    @GenericField(sortable = Sortable.YES)
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd") // <--- FORCER LE FORMAT ISO
    private LocalDate createdAt = LocalDate.now();

    @GenericField
    @Column(name = "in_vacations")
    private boolean inVacations;

    // --- CORRECTION ICI ---
    // On ajoute @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
    // Cela débloque le démarrage en disant à Hibernate d'ignorer la complexité de la formule SQL.
@Formula("(select count(*) from products p where p.shop_id = id)")
    private long nbProducts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shop", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OpeningHours> openingHours = new ArrayList<>();

    @OneToMany(mappedBy = "shop")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();
}