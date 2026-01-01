package fr.fullstack.shopapp.controller;

import fr.fullstack.shopapp.model.Category;
import fr.fullstack.shopapp.service.CategoryService;
import fr.fullstack.shopapp.util.ErrorValidation;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "*") // Autorise le front à se connecter [cite: 1]
public class CategoryController {

    @Autowired
    private CategoryService service;

    @Operation(summary = "Create a category") // Remplacement de @ApiOperation
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category, Errors errors) {
        if (errors.hasErrors()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ErrorValidation.getErrorValidationMessage(errors));
        }

        try {
            return ResponseEntity.ok(service.createCategory(category));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Delete a category by its id")
    @DeleteMapping("/{id}")
    public HttpStatus deleteCategory(@PathVariable long id) {
        try {
            service.deleteCategoryById(id);
            return HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get categories")
    @GetMapping
    // SpringDoc gère automatiquement la documentation de Pageable, pas besoin de @ApiImplicitParams
    public ResponseEntity<Page<Category>> getAllCategories(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.getCategoryList(pageable));
    }

    @Operation(summary = "Get a category by id")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(service.getCategoryById(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Update a category")
    @PutMapping
    public ResponseEntity<Category> updateCategory(@Valid @RequestBody Category category, Errors errors) {
        if (errors.hasErrors()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ErrorValidation.getErrorValidationMessage(errors));
        }

        try {
            return ResponseEntity.ok().body(service.updateCategory(category));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}