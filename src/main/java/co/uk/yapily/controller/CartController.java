package co.uk.yapily.controller;

import co.uk.yapily.dto.CartDto;
import co.uk.yapily.dto.CartItemDto;
import co.uk.yapily.dto.CartItemResponseDto;
import co.uk.yapily.dto.CheckoutDto;
import co.uk.yapily.exception.ApiException;
import co.uk.yapily.exception.ServiceException;
import co.uk.yapily.service.CartService;
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
@RequestMapping(value = "/carts")
@Slf4j
public class CartController {

  private final CartService service;

  /**
   * Constructor for CartController with service
   * @param service the service to connect to the CartService
   */
  public CartController(final CartService service) {
    this.service = service;
  }

  /**
   * Endpoint for saving a cart.
   *
   * @param dto The DTO representing the cart to be saved
   * @return ResponseEntity containing the saved cart DTO
   * @throws ApiException if an error occurs during the saving process
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Cart saved successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while saving the cart")
  })
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CartDto> saveCart(@Validated @RequestBody final CartDto dto) throws ApiException {
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
   * Endpoint for updating a cart with the provided CartItemDto and cart ID.
   * <p>
   * This method handles PUT requests to update a cart identified by the given ID with the information
   * provided in the CartItemDto. It validates the request body, processes the update operation through
   * the service layer, and returns a ResponseEntity containing the updated CartDto upon successful update.
   *
   * @param dto The CartItemDto containing the updated cart information.
   * @param id The ID of the cart to be updated.
   * @return ResponseEntity containing the updated CartDto and HTTP status OK if successful.
   * @throws ApiException If there are errors during the update process, wrapped with a meaningful message.
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cart updated successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while updating the cart")
  })
  @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CartDto> updateCart(@Validated @RequestBody final CartItemDto dto, @PathVariable(name = "id") final Long id)
  throws ApiException {
    try {
      final var response = service.update(dto, id);
      return ResponseEntity.status(HttpStatus.OK)
                           .contentType(MediaType.APPLICATION_JSON)
                           .body(response);
    } catch (final ServiceException e) {
      throw new ApiException(e.getMessage(), e);
    }
  }

  /**
   * Endpoint that initiates the checkout process for the cart identified by the specified ID.
   * <p>
   * This method sends a request to the service layer to initiate the checkout process for the cart
   * identified by the provided ID. Upon successful checkout, it returns a ResponseEntity with HTTP status 200 OK
   * and a body containing information about the checkout process, including any relevant details such as
   * finalized products and the total amount.
   *
   * @param id The ID of the cart to be checked out.
   * @return A ResponseEntity containing information about the checkout process, including any relevant details
   * such as finalized products and the total amount.
   * @throws ApiException If an unexpected error occurs during the checkout process, an ApiException is thrown
   * with a descriptive error message.
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cart checked out successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while checking out the cart")
  })
  @PostMapping(path = "/{id}/checkout")
  public ResponseEntity<CheckoutDto> checkoutCart(@PathVariable(name = "id") final Long id) throws ApiException {
    try {
      final var response = service.checkout(id);
      return ResponseEntity.status(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(response);
    } catch (final ServiceException e) {
      throw new ApiException(e.getMessage(), e);
    }
  }

  /**
   * Endpoint for delete a cart with the specified ID.
   *
   * @param id the ID of the cart to delete
   * @return a ResponseEntity with status 204 (No Content) if the deletion is successful
   * @throws ApiException if an unexpected error occurs while deleting the cart
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Cart deleted successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while deleting out the cart")
  })
  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteCart(@PathVariable(name = "id") final Long id) throws ApiException {
    try {
      service.delete(id);
      return ResponseEntity.noContent().build();
    } catch (final ServiceException e) {
      throw new ApiException("An unexpected error occurred while deleting cart with ID: " + id, e);
    }
  }

  /**
   * Endpoint for retrieving a cart with the specified ID.
   *
   * @param id the ID of the cart to retrieve
   * @return a ResponseEntity containing the CartDto representing the retrieved cart, with status 200 (OK) if found
   * @throws ApiException if an unexpected error occurs while retrieving the cart
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cart found successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while finding the cart")
  })
  @GetMapping(path = "/{id}")
  public ResponseEntity<CartDto> findCartById(@PathVariable(name = "id") final Long id) throws ApiException {
    try {
      final var response = service.findById(id);
      return ResponseEntity.ok(response);
    } catch (final ServiceException e) {
      throw new ApiException("An unexpected error occurred while finding cart with ID: " + id, e);
    }
  }

  /**
   * Endpoint for retrieving all carts.
   *
   * @return a ResponseEntity containing a list of CartDto representing all carts, with status 200 (OK) if found
   * @throws ApiException if an unexpected error occurs while retrieving all carts
   */
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "List of Carts found successfully"),
      @ApiResponse(responseCode = "500", description = "Problems encountered while finding list of carts")
  })
  @GetMapping
  public ResponseEntity<List<CartDto>> findAllCarts() throws ApiException {
    try {
      final var response = service.findAll();
      return ResponseEntity.ok(response);
    } catch (final ServiceException e) {
      throw new ApiException("An unexpected error occurred while finding all carts: ", e);
    }
  }
}
