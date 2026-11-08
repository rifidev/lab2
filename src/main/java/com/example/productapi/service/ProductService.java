package com.example.productapi.service;


import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.exception.InvalidQuantityException;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        Product saved = repository.save(product);
        return mapToResponse(saved);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getQuantity());
    }

    public ProductResponse getProductById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id: " + id));
        return mapToResponse(product);
    }

    public List<ProductResponse> searchProductsByName(String name) {
        List<Product> products = repository.filterByName(name);
        if((long) products.size() > 1000)
            throw new InvalidQuantityException("La quantité dépasse 1000");
        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductResponse> getAllProducts() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        Product updated = repository.save(product);
        return mapToResponse(updated);
    }

    public void deleteProduct(Long id) {
        if (!repository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Produit introuvable avec l'id: " + id);
        }
        repository.deleteById(id);
    }
}