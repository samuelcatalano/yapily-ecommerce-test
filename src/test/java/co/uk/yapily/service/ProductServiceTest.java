package co.uk.yapily.service;

import co.uk.yapily.dto.ProductDto;
import co.uk.yapily.entity.Product;
import co.uk.yapily.exception.ServiceException;
import co.uk.yapily.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class ProductServiceTest {

  private ProductService productService;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ModelMapper modelMapper;

  @BeforeEach
  public void setUp() {
    productService = new ProductService(productRepository, modelMapper);
  }

  @Test
  void test_save_product_Successful() throws ServiceException {
    // Given
    final ProductDto productDto = new ProductDto();
    productDto.setName("Test Product");

    final Product product = new Product();
    product.setName("Test Product");

    // When
    when(modelMapper.map(productDto, Product.class)).thenReturn(product);
    when(productRepository.save(product)).thenReturn(product);
    when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

    // Then
    final ProductDto savedProduct = productService.save(productDto);

    // Act & Assert
    assertEquals("Test Product", savedProduct.getName());
  }

  @Test
  void test_find_by_id_Successful() throws ServiceException {
    // Arrange
    final Long id = 123L; // Example ID
    final Product product = new Product(); // Example product entity
    final ProductDto productDto = new ProductDto(); // Example product DTO
    when(productRepository.findById(id)).thenReturn(Optional.of(product)); // Mocking repository behavior
    when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto); // Mocking modelMapper behavior

    // Act
    final ProductDto result = productService.findById(id);

    // Assert
    assertNotNull(result);
    assertEquals(productDto, result);
  }

  @Test
  void test_find_all_Successful() throws ServiceException {
    // Given
    final List<Product> products = new ArrayList<>(); // Example list of products
    products.add(new Product());
    products.add(new Product());

    final List<ProductDto> productDtos = new ArrayList<>(); // Example list of product DTOs
    productDtos.add(new ProductDto());
    productDtos.add(new ProductDto());

    // When
    when(productRepository.findAll()).thenReturn(products); // Mocking repository behavior
    when(modelMapper.map(products, new TypeToken<List<ProductDto>>() {
    }.getType())).thenReturn(productDtos); // Mocking modelMapper behavior

    // Then
    final List<ProductDto> result = productService.findAll();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(productDtos, result);
  }

  @Test
  void test_delete_Successful() throws ServiceException {
    // Given
    final Long id = 123L; // Example ID
    // Act
    productService.delete(id);
    // Assert
    verify(productRepository).deleteById(id);
  }

  @Test
  void test_save_product_DataIntegrityViolation() throws ServiceException {
    // Given
    final ProductDto productDto = new ProductDto();
    final Product product = new Product();

    // When
    when(modelMapper.map(productDto, Product.class)).thenReturn(product);
    when(productRepository.save(product)).thenThrow(DataIntegrityViolationException.class);

    // Act & Assert
    assertThrows(ServiceException.class, () -> productService.save(productDto));
  }

  @Test
  void test_delete_ShouldLogWarningWhenDeletingNonExistentProduct() {
    // Arrange
    final Long id = 456L; // Example non-existent ID
    doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(id); // Mocking repository behavior
    // Act
    try {
      productService.delete(id);
    } catch (final ServiceException e) {
      // Ignoring ServiceException as it's expected in this scenario
    }
    // Assert
    verify(productRepository).deleteById(id);
  }

  @Test
  void test_find_by_id_ShouldThrowServiceExceptionWhenIdIsNull() {
    // Act & Assert
    assertThrows(ServiceException.class, () -> productService.findById(null));
  }

  @Test
  void test_find_by_id_ShouldThrowServiceExceptionWhenProductNotFound() {
    // Arrange
    final Long id = 111L; // Example ID
    when(productRepository.findById(id)).thenReturn(Optional.empty()); // Mocking repository behavior
    // Act & Assert
    assertThrows(ServiceException.class, () -> productService.findById(id));
  }

  @Test
  void test_find_by_id_ShouldThrowServiceExceptionOnDataRetrievalFailureException() {
    // Arrange
    final Long id = 111L; // Example ID
    when(productRepository.findById(id)).thenThrow(DataRetrievalFailureException.class); // Mocking repository behavior
    // Act & Assert
    assertThrows(ServiceException.class, () -> productService.findById(id));
  }

  @Test
  void test_find_all_shouldReturnNullWhenNoProductsFound() throws ServiceException {
    // Arrange
    when(productRepository.findAll()).thenReturn(new ArrayList<>()); // Mocking repository behavior
    // Act
    final List<ProductDto> result = productService.findAll();
    // Assert
    assertNull(result); // find all products will return null
  }
}
