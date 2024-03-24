package co.uk.yapily.service;

import co.uk.yapily.dto.ProductDto;
import co.uk.yapily.entity.Product;
import co.uk.yapily.exception.ServiceException;
import co.uk.yapily.repository.ProductRepository;
import co.uk.yapily.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The ProductService class provides business logic for managing products.
 *
 * @author Samuel Catalano
 * @since 1.0.0
 */
@Slf4j
@Service
public class ProductService implements BaseService<ProductDto> {

  private final ProductRepository repository;
  private final ModelMapper modelMapper;

  /**
   * Creates a new product service.
   * @param repository the product repository
   */
  public ProductService(final ProductRepository repository, final ModelMapper modelMapper) {
    this.repository = repository;
    this.modelMapper = modelMapper;
  }

  /**
   * Saves a new product.
   *
   * @param dto the product data
   * @return the saved product
   * @throws ServiceException if an error occurs
   */
  public ProductDto save(final ProductDto dto) throws ServiceException {
    try {
      final Product product = modelMapper.map(dto, Product.class);
      final Product savedProduct = repository.save(product);
      // Map the persisted entity back to a DTO and return
      return modelMapper.map(savedProduct, ProductDto.class);
    } catch (final DataIntegrityViolationException e) {
      log.error("Error persisting new product due to data integrity violation: {}", e.getMessage(), e);
      throw new ServiceException("Error persisting product: It is not possible to insert 2 or more products with exactly the same name!", e);
    } catch (final DataAccessException e) {
      log.error("Error persisting new product! Reason: {}", e.getMessage(), e);
      throw new ServiceException("Error persisting product: ", e);
    } catch (final Exception e) {
      log.error("An unexpected error occurred while persisting product! Reason: {}", e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while persisting product: ", e);
    }
  }

  /**
   * Deletes an existing product.
   *
   * @param id the product ID
   * @throws ServiceException if an error occurs
   */
  @Override
  public void delete(final Long id) throws ServiceException {
    try {
      repository.deleteById(id);
    } catch (final EmptyResultDataAccessException e) {
      log.warn("Attempted to delete non-existent product with ID: {}", id);
    } catch (final DataAccessException e) {
      log.error("Error deleting product with ID: {}. Reason: {}", id, e.getMessage(), e);
      throw new ServiceException("Error deleting product with ID: " + id, e);
    } catch (final Exception e) {
      log.error("An unexpected error occurred while deleting product with ID: {}. Reason: {}", id, e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while deleting product with ID: " + id, e);
    }
  }

  /**
   * Returns a list of all products.
   *
   * @return a list of products
   * @throws ServiceException if an error occurs
   */
  @Override
  public List<ProductDto> findAll() throws ServiceException {
    try {
      final List<Product> products = repository.findAll();
      // Map the list of product entities to a list of DTOs using ModelMapper
      return modelMapper.map(products, new TypeToken<List<ProductDto>>(){}.getType());
    } catch (final DataAccessException e) {
      log.error("Error retrieving list of products! Reason: {}", e.getMessage(), e);
      throw new ServiceException("Error retrieving list of products! Reason: ", e);
    } catch (final Exception e) {
      log.error("An unexpected error occurred while retrieving list of products! Reason: {}", e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while retrieving list of products! Reason: ", e);
    }
  }

  /**
   * Retrieves a product by its ID.
   *
   * @param id the ID of the product to retrieve
   * @return the retrieved product DTO
   * @throws ServiceException if the product with the given ID is not found or an error occurs during retrieval
   */
  @Override
  public ProductDto findById(final Long id) throws ServiceException {
    final Long productId = Optional.ofNullable(id).orElseThrow(() -> new ServiceException("Product ID cannot be null"));

    try {
      final Optional<Product> productOptional = repository.findById(productId);
      final Product product = productOptional.orElseThrow(() -> new ServiceException("Product not found with ID: " + productId));
      // Map the retrieved product entity to a DTO using ModelMapper
      return modelMapper.map(product, ProductDto.class);
    } catch (final DataAccessException e) {
      log.error("Error retrieving product with ID: {}. Reason: {}", id, e.getMessage(), e);
      throw new ServiceException("Error retrieving product with ID: " + id, e);
    } catch (final Exception e) {
      log.error("An unexpected error occurred while retrieving product with ID: {}. Reason: {}", id, e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while retrieving product with ID: " + id, e);
    }
  }
}
