package co.uk.yapily.controller;

import co.uk.yapily.dto.ProductDto;
import co.uk.yapily.exception.ApiException;
import co.uk.yapily.exception.ServiceException;
import co.uk.yapily.service.ProductService;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/products")
@Slf4j
public class ProductController {

  private final ProductService service;

  /**
   * Constructor for ProductController with service
   * @param service the service to connect to the ProductService
   */
  public ProductController(final ProductService service) {
    this.service = service;
  }

  /**
   * Endpoint for saving a product.
   *
   * @param dto The DTO representing the product to be saved
   * @return ResponseEntity containing the saved product DTO
   * @throws ApiException if an error occurs during the saving process
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Product saved successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while saving the product")
  })
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProductDto> saveProduct(@Validated @RequestBody final ProductDto dto) throws ApiException {
    try {
      final var response = service.save(dto);
      return ResponseEntity.status(HttpStatus.CREATED)
                           .contentType(MediaType.APPLICATION_JSON)
                           .body(response);
    } catch (final ServiceException e) {
      throw new ApiException(e.getMessage(), e);
    }
  }

  /**
   * Endpoint for delete a product with the specified ID.
   *
   * @param id the ID of the product to delete
   * @return a ResponseEntity with status 204 (No Content) if the deletion is successful
   * @throws ApiException if an unexpected error occurs while deleting the product
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while deleting the product")
  })
  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") final Long id) throws ApiException {
    try {
      service.delete(id);
      return ResponseEntity.noContent().build();
    } catch (final ServiceException e) {
      throw new ApiException("An unexpected error occurred while deleting product with ID: " + id, e);
    }
  }

  /**
   * Endpoint for retrieving a product with the specified ID.
   *
   * @param id the ID of the product to retrieve
   * @return a ResponseEntity containing the ProductDto representing the retrieved product, with status 200 (OK) if found
   * @throws ApiException if an unexpected error occurs while retrieving the product
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product found successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while finding the product")
  })
  @GetMapping(path = "/{id}")
  public ResponseEntity<ProductDto> findProductById(@PathVariable(name = "id") final Long id) throws ApiException {
    try {
      final var response = service.findById(id);
      return ResponseEntity.ok(response);
    } catch (final ServiceException e) {
      throw new ApiException("An unexpected error occurred while finding product with ID: " + id, e);
    }
  }

  /**
   * Endpoint for retrieving all products.
   *
   * @return a ResponseEntity containing a list of ProductDto representing all products, with status 200 (OK) if found
   * @throws ApiException if an unexpected error occurs while retrieving all products
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "List of Products found successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while finding list of products")
  })
  @GetMapping
  public ResponseEntity<List<ProductDto>> findAllProducts() throws ApiException {
    try {
      final var response = service.findAll();
      return ResponseEntity.ok(response);
    } catch (final ServiceException e) {
      throw new ApiException("An unexpected error occurred while finding all products: ", e);
    }
  }
}
