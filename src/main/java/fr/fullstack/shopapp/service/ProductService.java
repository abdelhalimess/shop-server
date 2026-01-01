package fr.fullstack.shopapp.service;

import fr.fullstack.shopapp.model.LocalizedProduct;
import fr.fullstack.shopapp.model.Product;
// Assurez-vous d'importer votre Enum Locale (adaptez le package si besoin)
import fr.fullstack.shopapp.model.Locale; 
import fr.fullstack.shopapp.repository.ProductRepository;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@Service
public class ProductService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Product createProduct(Product product) throws Exception {
        // 1. Vérifications (Nom FR obligatoire)
        checkLocalizedProducts(product);

        // 2. CORRECTION CRUCIALE : Lier les traductions au produit
        // Sans ça, product_id reste NULL dans la table localized_product
        if (product.getLocalizedProducts() != null) {
            product.getLocalizedProducts().forEach(lp -> lp.setProduct(product));
        }

        Product newProduct = productRepository.save(product);
        
        // Force la synchronisation pour s'assurer que tout est stocké
        em.flush();
        em.refresh(newProduct);
        return newProduct;
    }

    @Transactional
    public void deleteProductById(long id) throws Exception {
        if (!productRepository.existsById(id)) {
            throw new Exception("Product with id " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    public Product getProductById(long id) throws Exception {
        return getProduct(id);
    }

    public Page<Product> getShopProductList(Optional<Long> shopId, Optional<Long> categoryId, Pageable pageable) {
        if (shopId.isPresent() && categoryId.isPresent()) {
            return productRepository.findByShopAndCategory(shopId.get(), categoryId.get(), pageable);
        }
        if (shopId.isPresent()) {
            return productRepository.findByShop(shopId.get(), pageable);
        }
        return productRepository.findByOrderByIdAsc(pageable);
    }

    @Transactional
    public Product updateProduct(Product product) throws Exception {
        // Vérifie l'existence
        getProduct(product.getId());
        
        // Même correction ici pour la mise à jour (Liaison Parent-Enfant)
        if (product.getLocalizedProducts() != null) {
            product.getLocalizedProducts().forEach(lp -> lp.setProduct(product));
        }
        
        return productRepository.save(product);
    }

    private void checkLocalizedProducts(Product product) throws Exception {
        if (product.getLocalizedProducts() == null || product.getLocalizedProducts().isEmpty()) {
             throw new Exception("Localized products list cannot be null or empty");
        }

        // CORRECTION DU BUG "A name in french..."
        // On compare l'Enum Locale.FR (et non la String "FR")
        // Assurez-vous que Locale.FR correspond bien à votre définition d'Enum
        Optional<LocalizedProduct> localizedProductFr = product.getLocalizedProducts()
                .stream()
                .filter(o -> Locale.FR.equals(o.getLocale())) // Utilisation de l'Enum
                .findFirst();

        if (localizedProductFr.isEmpty()) {
            throw new Exception("A name in french must be at least provided");
        }
    }

    private Product getProduct(Long id) throws Exception {
        return productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProduct(String input, Pageable pageable) {
        SearchSession searchSession = Search.session(em);

        SearchResult<Product> result = searchSession.search(Product.class)
                .where(f -> f.bool()
                        .must(f.match()
                                .field("localizedProducts.name") // <--- On cherche dans le sous-objet
                                .matching(input)
                                .fuzzy(2) // Tolère 2 fautes de frappe
                        )
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }
}