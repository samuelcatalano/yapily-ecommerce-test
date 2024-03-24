package co.uk.yapily.entity;

import co.uk.yapily.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This class contains JUnit test methods for testing the functionality of the ProductRepository.
 */
class ProductTest {

  /**
   * Test case to verify the save operation of a product in the repository.
   */
  @Test
  void test_save_product() {
    final ProductRepository productRepository = mock(ProductRepository.class);
    final Product product = Product.builder()
        .name("Test Product")
        .price(10.0)
        .addedAt(Date.from(Instant.now()))
        .labels(List.of("food", "limited"))
        .build();

    // Mock behavior and perform save operation
    when(productRepository.save(any(Product.class))).thenReturn(product);
    final Product savedProduct = productRepository.save(product);

    // Assertions and verifications
    assertEquals("Test Product", savedProduct.getName());
    assertEquals(10.0, savedProduct.getPrice());
    assertEquals(List.of("food", "limited"), savedProduct.getLabels());
    assertNotNull(savedProduct.getAddedAt());
    verify(productRepository, times(1)).save(any(Product.class));
  }

  /**
   * Test case to verify the update operation of a product in the repository.
   */
  @Test
  void test_update_product() {
    final ProductRepository productRepository = mock(ProductRepository.class);
    final Product product = Product.builder()
        .id(1L)
        .name("Test Product")
        .price(10.0)
        .labels(List.of("limited", "drink"))
        .build();

    // Mock behavior and perform update operation
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productRepository.save(any(Product.class))).thenReturn(product);
    final Product updatedProduct = productRepository.findById(1L).map(p -> {
      p.setName("Updated Product");
      p.setPrice(20.0);
      return productRepository.save(p);
    }).orElse(null);

    // Assertions and verifications
    assertNotNull(updatedProduct);
    assertEquals(1L, updatedProduct.getId());
    assertEquals("Updated Product", updatedProduct.getName());
    assertEquals(20.0, updatedProduct.getPrice());
    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(1)).save(any(Product.class));
  }

  /**
   * Test case to verify the delete operation of a product in the repository.
   */
  @Test
  void test_delete_product() {
    final ProductRepository productRepository = mock(ProductRepository.class);

    // Mock behavior and perform delete operation
    doNothing().when(productRepository).deleteById(1L);
    productRepository.deleteById(1L);

    // Verification
    verify(productRepository, times(1)).deleteById(1L);
  }

  /**
   * Test case to verify finding a product by its id in the repository.
   */
  @Test
  void test_find_by_id() {
    final ProductRepository productRepository = mock(ProductRepository.class);
    final Product product = Product.builder()
        .id(1L)
        .name("Test Product")
        .price(10.0)
        .labels(List.of("clothes", "limited"))
        .build();

    // Mock behavior and perform find by id operation
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    // Assertions and verifications
    final Optional<Product> foundProduct = productRepository.findById(1L);

    assertNotNull(foundProduct);
    assertTrue(foundProduct.isPresent());
    assertEquals(product.getId(), foundProduct.get().getId());
    assertEquals(product.getName(), foundProduct.get().getName());
    assertEquals(product.getPrice(), foundProduct.get().getPrice());
    assertEquals(product.getLabels(), foundProduct.get().getLabels());
    verify(productRepository, times(1)).findById(1L);
  }
}
