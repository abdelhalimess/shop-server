package fr.fullstack.shopapp.controller;

import fr.fullstack.shopapp.model.Shop;
import fr.fullstack.shopapp.service.ShopService;
import fr.fullstack.shopapp.util.ErrorValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shops")
@CrossOrigin(origins = "*") // E_PRO_180
public class ShopController {

    @Autowired
    private ShopService service;

    @Operation(summary = "Create a shop")
    @PostMapping
    public ResponseEntity<Shop> createShop(@Valid @RequestBody Shop shop, Errors errors) {
        if (errors.hasErrors()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ErrorValidation.getErrorValidationMessage(errors));
        }

        try {
            return ResponseEntity.ok(service.createShop(shop));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Delete a shop by its id")
    @DeleteMapping("/{id}")
    public HttpStatus deleteShop(@PathVariable long id) {
        try {
            service.deleteShopById(id);
            return HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // E_BTQ_65: Recherche Plein Texte via Elasticsearch
    // IMPORTANT : Placée avant getShopById pour éviter les conflits
    @Operation(summary = "Search shops using Elasticsearch (Full Text Search)")
    @GetMapping("/search")
    public ResponseEntity<Page<Shop>> searchShops(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "input") String input,
            @RequestParam(value = "inVacations", required = false) Optional<Boolean> inVacations,
            @RequestParam(value = "createdAfter", required = false) Optional<String> createdAfter,
            @RequestParam(value = "createdBefore", required = false) Optional<String> createdBefore
    ) {
        // Correction ici : Suppression des doubles accolades {{ }}
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Shop> shopList = service.searchShop(input, inVacations, createdAfter, createdBefore, pageable);
            return ResponseEntity.ok().body(shopList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // E_BTQ_50: Recherche paginée standard (SQL)
    @Operation(summary = "Get shops (sorting and filtering are possible)")
    @GetMapping
    public ResponseEntity<Page<Shop>> getAllShops(
            @ParameterObject Pageable pageable,
            @Parameter(description = "To sort the shops. Possible values are 'name', 'nbProducts' and 'createdAt'", example = "name")
            @RequestParam(required = false) Optional<String> sortBy,
            @Parameter(description = "Define that the shops must be in vacations or not", example = "true")
            @RequestParam(required = false) Optional<Boolean> inVacations,
            @Parameter(description = "Define that the shops must be created after this date", example = "2022-11-15")
            @RequestParam(required = false) Optional<String> createdAfter,
            @Parameter(description = "Define that the shops must be created before this date", example = "2022-11-15")
            @RequestParam(required = false) Optional<String> createdBefore
    ) {
        return ResponseEntity.ok(
                service.getShopList(sortBy, inVacations, createdAfter, createdBefore, pageable)
        );
    }

    @Operation(summary = "Get a shop by id")
    // Regex :\\d+ force l'id à être un chiffre. "search" ne passera pas ici.
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Shop> getShopById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(service.getShopById(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Update a shop")
@PutMapping // C'est PUT pour une modification complète
    public ResponseEntity<Shop> updateShop(@RequestBody @Valid Shop shop) {
        try {
            // On appelle le service qui va faire la vérification E_FIX_10
            Shop updatedShop = service.updateShop(shop);
            return ResponseEntity.ok(updatedShop);
        } catch (Exception e) {
            // Si E_FIX_10 échoue (conflit horaire), on renvoie une erreur 400 (Bad Request)
            // avec le message d'erreur expliquant le conflit.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}