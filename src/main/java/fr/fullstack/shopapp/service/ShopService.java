package fr.fullstack.shopapp.service;

import fr.fullstack.shopapp.model.OpeningHours;
import fr.fullstack.shopapp.model.Product;
import fr.fullstack.shopapp.model.Shop;
import fr.fullstack.shopapp.repository.ShopRepository;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ShopRepository shopRepository;

@Transactional
    public Shop createShop(Shop shop) throws Exception {
        // 1. Validation des horaires (E_FIX_10)
        checkOpeningHours(shop.getOpeningHours());

        // 2. CORRECTION IMPORTANTE : Lier les enfants au parent
        // Sans ça, les horaires sont orphelins (shop_id = null) et disparaissent.
        if (shop.getOpeningHours() != null) {
            shop.getOpeningHours().forEach(hours -> hours.setShop(shop));
        }

        // 3. Sauvegarde
        Shop newShop = shopRepository.save(shop);
        
        // 4. Refresh pour les formules calculées
        em.flush();
        em.refresh(newShop);
        
        return newShop;
    }

    @Transactional
    public void deleteShopById(long id) throws Exception {
        Shop shop = getShop(id);
        deleteNestedRelations(shop);
        shopRepository.deleteById(id);
    }

    public Shop getShopById(long id) throws Exception {
        return getShop(id);
    }

    // E_BTQ_50: Recherche standard SQL
    public Page<Shop> getShopList(
            Optional<String> sortBy,
            Optional<Boolean> inVacations,
            Optional<String> createdAfter,
            Optional<String> createdBefore,
            Pageable pageable
    ) {
        if (sortBy.isPresent()) {
            switch (sortBy.get()) {
                case "name": return shopRepository.findByOrderByNameAsc(pageable);
                case "createdAt": return shopRepository.findByOrderByCreatedAtAsc(pageable);
                default: return shopRepository.findByOrderByNbProductsAsc(pageable);
            }
        }

        Page<Shop> shopList = getShopListWithFilter(inVacations, createdAfter, createdBefore, pageable);
        if (shopList != null) {
            return shopList;
        }

        return shopRepository.findByOrderByIdAsc(pageable);
    }

    /**
     * E_BTQ_65: Recherche Elasticsearch CORRIGÉE
     * Cette version utilise (f, root) pour ajouter des filtres conditionnellement.
     * Cela évite d'envoyer 'null' à Elasticsearch.
     */
    @Transactional(readOnly = true)
    public Page<Shop> searchShop(
            String input,
            Optional<Boolean> inVacations,
            Optional<String> createdAfter,
            Optional<String> createdBefore,
            Pageable pageable
    ) {
        SearchSession searchSession = Search.session(em);

        SearchResult<Shop> result = searchSession.search(Shop.class)
                .where((f, root) -> {
                    // 1. Recherche Textuelle (Obligatoire si input présent, sinon matchAll)
                    // On met fuzzy(2) pour tolérer les fautes de frappe
                    root.add(f.match().field("name").matching(input).fuzzy(2));

                    // 2. Filtre Vacances (Seulement si paramètre présent !)
                    if (inVacations.isPresent()) {
                        root.add(f.match().field("inVacations").matching(inVacations.get()));
                    }

                    // 3. Filtre Date Après (Seulement si présent)
                    if (createdAfter.isPresent()) {
                        root.add(f.range().field("createdAt")
                                .greaterThan(LocalDate.parse(createdAfter.get())));
                    }

                    // 4. Filtre Date Avant (Seulement si présent)
                    if (createdBefore.isPresent()) {
                        root.add(f.range().field("createdAt")
                                .lessThan(LocalDate.parse(createdBefore.get())));
                    }
                })
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

@Transactional
    public Shop updateShop(Shop shop) throws Exception {
        getShop(shop.getId()); // Vérif existence
        checkOpeningHours(shop.getOpeningHours());

        // CORRECTION IMPORTANTE ICI AUSSI
        if (shop.getOpeningHours() != null) {
            shop.getOpeningHours().forEach(hours -> hours.setShop(shop));
        }

        return shopRepository.save(shop);
    }
    private void deleteNestedRelations(Shop shop) {
        List<Product> products = shop.getProducts();
        if (products == null) return;
        for (Product product : products) {
            product.setShop(null);
            em.merge(product);
        }
    }

    private Shop getShop(Long id) throws Exception {
        return shopRepository.findById(id)
                .orElseThrow(() -> new Exception("Shop with id " + id + " not found"));
    }

    private Page<Shop> getShopListWithFilter(
            Optional<Boolean> inVacations,
            Optional<String> createdAfter,
            Optional<String> createdBefore,
            Pageable pageable
    ) {
        // ... (votre logique SQL existante, inchangée) ...
        if (inVacations.isPresent() && createdBefore.isPresent() && createdAfter.isPresent()) {
            return shopRepository.findByInVacationsAndCreatedAtGreaterThanAndCreatedAtLessThan(
                    inVacations.get(), LocalDate.parse(createdAfter.get()), LocalDate.parse(createdBefore.get()), pageable);
        }
        if (inVacations.isPresent() && createdBefore.isPresent()) {
            return shopRepository.findByInVacationsAndCreatedAtLessThan(
                    inVacations.get(), LocalDate.parse(createdBefore.get()), pageable);
        }
        if (inVacations.isPresent() && createdAfter.isPresent()) {
            return shopRepository.findByInVacationsAndCreatedAtGreaterThan(
                    inVacations.get(), LocalDate.parse(createdAfter.get()), pageable);
        }
        if (inVacations.isPresent()) {
            return shopRepository.findByInVacations(inVacations.get(), pageable);
        }
        if (createdBefore.isPresent() && createdAfter.isPresent()) {
            return shopRepository.findByCreatedAtBetween(
                    LocalDate.parse(createdAfter.get()), LocalDate.parse(createdBefore.get()), pageable);
        }
        if (createdBefore.isPresent()) {
            return shopRepository.findByCreatedAtLessThan(LocalDate.parse(createdBefore.get()), pageable);
        }
        if (createdAfter.isPresent()) {
            return shopRepository.findByCreatedAtGreaterThan(LocalDate.parse(createdAfter.get()), pageable);
        }
        return null;
    }

    /**
     * E_FIX_10 : Vérifie qu'aucun horaire ne se chevauche.
     * Algorithme :
     * 1. On boucle sur les 7 jours de la semaine.
     * 2. On récupère les horaires du jour J.
     * 3. On les trie par heure d'ouverture (8h, puis 10h, puis 14h...).
     * 4. On regarde si l'horaire (i+1) commence AVANT que l'horaire (i) ne finisse.
     */
    private void checkOpeningHours(List<OpeningHours> openingHours) throws Exception {
        if (openingHours == null || openingHours.isEmpty()) {
            return;
        }

        for (int day = 1; day <= 7; day++) {
            int currentDay = day; // Variable finale pour le lambda
            
            // On récupère et on trie les horaires de ce jour-là
            List<OpeningHours> dayHours = openingHours.stream()
                    .filter(o -> o.getDay() == currentDay)
                    .sorted(Comparator.comparing(OpeningHours::getOpenAt))
                    .toList(); // .collect(Collectors.toList()) si Java < 16

            // Comparaison deux par deux
            for (int i = 0; i < dayHours.size() - 1; i++) {
                OpeningHours current = dayHours.get(i);
                OpeningHours next = dayHours.get(i + 1);

                // LE TEST CRITIQUE : Chevauchement
                // Exemple : Current [8h - 12h], Next [11h - 15h]
                // 11h est avant 12h -> BOUM, Exception.
                if (next.getOpenAt().isBefore(current.getCloseAt())) {
                    throw new Exception("Conflit d'horaires détecté pour le jour " + currentDay + 
                            " : Le créneau de " + next.getOpenAt() + " commence avant la fin de celui de " + current.getCloseAt());
                }
            }
        }
    }
}