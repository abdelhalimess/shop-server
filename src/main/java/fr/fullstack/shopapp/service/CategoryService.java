package fr.fullstack.shopapp.service;

import fr.fullstack.shopapp.model.Category;
import fr.fullstack.shopapp.model.Product;
import fr.fullstack.shopapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategoryById(long id) throws Exception {
        Category category = getCategory(id);
        // delete nested relations with products
        deleteNestedRelations(category);
        categoryRepository.deleteById(id);
    }

    public Category getCategoryById(long id) throws Exception {
        return getCategory(id);
    }

    public Page<Category> getCategoryList(Pageable pageable) {
        return categoryRepository.findByOrderByIdAsc(pageable);
    }

    @Transactional
    public Category updateCategory(Category category) throws Exception {
        // Vérifie si la catégorie existe avant mise à jour
        getCategory(category.getId());
        return categoryRepository.save(category);
    }

    private void deleteNestedRelations(Category category) {
        List<Product> products = category.getProducts();
        // On évite les NullPointerException
        if (products == null) {
            return;
        }
        
        // Mise à jour de la relation ManyToMany côté Propriétaire (Product)
        for (Product product : products) {
            product.getCategories().remove(category);
            em.merge(product);
        }
        // Le flush est géré automatiquement par la fin de la transaction @Transactional
    }

    private Category getCategory(Long id) throws Exception {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new Exception("Category with id " + id + " not found"));
    }
}