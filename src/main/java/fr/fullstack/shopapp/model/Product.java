package fr.fullstack.shopapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "products")
@Indexed // Dit à Elasticsearch : "Ceci est un objet consultable"
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Prix en centimes (Exigence E_PRD_15)
    @GenericField // Utile si on veut trier/filtrer par prix plus tard
    @Column(name = "price")
private long price; // Stocké en centimes (1250)

    @JsonGetter("price")
    public double getPriceInEuros() {
        return this.price / 100.0; // Envoie 12.50 au front
    }

    @JsonSetter("price")
    public void setPriceFromEuros(double priceInEuros) {
        this.price = Math.round(priceInEuros * 100); // Reçoit 12.50 du front, stocke 1250
    }

    // Relation avec la Boutique (Plusieurs produits pour une boutique)
    @ManyToOne
    private Shop shop;

    // Relation avec les Catégories
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "products_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    // --- CORRECTION MAJEURE ICI ---
    // 1. mappedBy = "product" : Fait le lien avec le champ 'product' ajouté dans LocalizedProduct.java
    // 2. cascade = ALL : Si je supprime le produit, je supprime ses traductions.
    // 3. @IndexedEmbedded : Dit à Elasticsearch d'inclure le contenu des traductions (nom, description) dans l'index du produit pour la recherche.
@OneToMany(cascade = CascadeType.ALL, mappedBy = "product", fetch = FetchType.EAGER)
    @IndexedEmbedded // On inclut les champs indexés des enfants dans la recherche du parent
    private List<LocalizedProduct> localizedProducts = new ArrayList<>();
    // Méthode utilitaire pour lier proprement les deux objets (Bonne pratique JPA)
    public void addLocalizedProduct(LocalizedProduct localizedProduct) {
        localizedProducts.add(localizedProduct);
        localizedProduct.setProduct(this);
    }
}