package co.uk.yapily.service;

import co.uk.yapily.dto.*;
import co.uk.yapily.entity.Cart;
import co.uk.yapily.entity.Product;
import co.uk.yapily.exception.ServiceException;
import co.uk.yapily.repository.CartRepository;
import co.uk.yapily.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The CartService class provides business logic for managing carts.
 *
 * @author Samuel Catalano
 * @since 1.0.0
 */
@Slf4j
@Service
public class CartService implements BaseService<CartDto> {

  private final CartRepository repository;
  private final ProductService productService;
  private final ModelMapper modelMapper;

  /**
   * Creates a new cart service.
   *
   * @param repository the cart repository
   */
  public CartService(final CartRepository repository, final ProductService productService, final ModelMapper modelMapper) {
    this.repository = repository;
    this.productService = productService;
    this.modelMapper = modelMapper;
  }

  /**
   * Saves a new cart.
   *
   * @param dto the cart data
   * @return the saved cart
   * @throws ServiceException if an error occurs
   */
  @Override
  public CartDto save(final CartDto dto) throws ServiceException {
    try {
      final Cart cart = modelMapper.map(dto, Cart.class);
      final Cart savedCart = repository.save(cart);
      // Map the persisted entity back to a DTO and return
      return modelMapper.map(savedCart, CartDto.class);
    } catch (final DataIntegrityViolationException e) {
      log.error("Error persisting new cart due to data integrity violation: {}", e.getMessage(), e);
      throw new ServiceException("Error persisting cart: ", e);
    } catch (final DataAccessException e) {
      log.error("Error persisting new cart! Reason: {}", e.getMessage(), e);
      throw new ServiceException("Error persisting cart: ", e);
    } catch (final Exception e) {
      log.error("An unexpected error occurred while persisting cart! Reason: {}", e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while persisting cart: ", e);
    }
  }

  /**
   * Updates a cart with the provided CartItemDto and cart ID.
   * This method updates the specified cart with the products from the given CartItemDto
   * and sets the checkout status to false.
   *
   * @param dto The CartItemDto containing product information to update the cart.
   * @param id  The ID of the cart to be updated.
   * @return The updated CartDto after saving changes.
   * @throws ServiceException If there are errors during the update process.
   */
  public CartDto update(final CartItemDto dto, final Long id) throws ServiceException {
    try {
      final Long cartId = Optional.ofNullable(id).orElseThrow(() -> new ServiceException("Cart id cannot be null"));

      final ProductDto productDto = productService.findById(dto.getProductId());
      final Product product = modelMapper.map(productDto, Product.class);
      final Optional<Cart> optionalCart = repository.findById(cartId);
      final Cart cart = optionalCart.orElseThrow(() -> new ServiceException("Cart not found with id: " + cartId));

      if (cart.isCheckout()) {
        throw new ServiceException("You can't add more products because the cart is already checked out!");
      }

      addProductsToCart(dto, cart, product);
      cart.setCheckout(false); // still not checked-out

      final Cart updatedCart = repository.save(cart);
      final var updatedCartDto = modelMapper.map(updatedCart, CartDto.class);
      // Map products to CartProductDto
      mapProductsToCartProductDto(updatedCart, updatedCartDto);
      return updatedCartDto;
    } catch (final Exception e) {
      log.error("An unexpected error occurred while persisting cart! Reason: {}", e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while persisting cart! Reason: " + e.getMessage(), e);
    }
  }

  /**
   * Performs the checkout operation for a given cart.
   *
   * @param id The ID of the cart to be checked out. Must not be null.
   * @return A CheckoutDto object containing the checked out cart and the total amount.
   * @throws ServiceException If the cart ID is null, if no cart is found with the given ID, if the cart is already checked out, or if an
   * unexpected error occurs during the checkout process.
   */
  public CheckoutDto checkout(final Long id) throws ServiceException {
    try {
      final Long cartId = Optional.ofNullable(id).orElseThrow(() -> new ServiceException("Cart with id cannot be null"));
      final Optional<Cart> optionalCart = repository.findById(cartId);
      final Cart cart = optionalCart.orElseThrow(() -> new ServiceException("Cart not found with id: " + cartId));

      if (cart.isCheckout()) {
        throw new ServiceException("Cart is already checked out!");
      }
      cart.setCheckout(true);
      cart.setAmount(calculateCartAmount(cart));
      final var checkedOutCart = repository.save(cart);

      final CartDto cartDto = modelMapper.map(checkedOutCart, CartDto.class);
      mapProductsToCartProductDto(checkedOutCart, cartDto);

      return CheckoutDto.builder().cart(cartDto).amount(checkedOutCart.getAmount()).build();
    } catch (final Exception e) {
      log.error("An unexpected error occurred while checking out cart! Reason: {}", e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while checking out cart: Reason: " + e.getMessage(), e);
    }
  }

  /**
   * Deletes an existing cart.
   *
   * @param id the cart ID
   * @throws ServiceException if an error occurs
   */
  @Override
  public void delete(final Long id) throws ServiceException {
    try {
      repository.deleteById(id);
    } catch (final EmptyResultDataAccessException e) {
      log.warn("Attempted to delete non-existent cart with ID: {}", id);
    } catch (final DataAccessException e) {
      log.error("Error deleting cart with ID: {}. Reason: {}", id, e.getMessage(), e);
      throw new ServiceException("Error deleting cart with ID: " + id, e);
    } catch (final Exception e) {
      log.error("An unexpected error occurred while deleting cart with ID: {}. Reason: {}", id, e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while deleting cart with ID: " + id, e);
    }
  }

  /**
   * Returns a list of all carts.
   *
   * @return a list of carts
   * @throws ServiceException if an error occurs
   */
  @Override
  public List<CartDto> findAll() throws ServiceException {
    try {
      final List<Cart> carts = repository.findAll();
      // Map the list of cart entities to a list of DTOs using ModelMapper
      final List<CartDto> cartsDto = modelMapper.map(carts, new TypeToken<List<CartDto>>() {
      }.getType());
      mapListOfProductsToCartProductDto(cartsDto, carts);
      return cartsDto;
    } catch (final DataAccessException e) {
      log.error("Error retrieving list of carts! Reason: {}", e.getMessage(), e);
      throw new ServiceException("Error retrieving list of carts! Reason: ", e);
    } catch (final Exception e) {
      log.error("An unexpected error occurred while retrieving list of carts! Reason: {}", e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while retrieving list of carts! Reason: ", e);
    }
  }

  /**
   * Retrieves a cart by its ID.
   *
   * @param id the ID of the cart to retrieve
   * @return the retrieved cart DTO
   * @throws ServiceException if the cart with the given ID is not found or an error occurs during retrieval
   */
  @Override
  public CartDto findById(final Long id) throws ServiceException {
    final Long cartId = Optional.ofNullable(id).orElseThrow(() -> new ServiceException("Cart ID cannot be null"));
    try {
      final Optional<Cart> cartOptional = repository.findById(cartId);
      final Cart cart = cartOptional.orElseThrow(() -> new ServiceException("Cart not found with ID: " + cartId));
      // Map the retrieved cart entity to a DTO using ModelMapper
      final var cartDto = modelMapper.map(cart, CartDto.class);
      // Map products to CartProductDto
      mapProductsToCartProductDto(cart, cartDto);
      return cartDto;
    } catch (final DataAccessException e) {
      log.error("Error retrieving cart with ID: {}. Reason: {}", id, e.getMessage(), e);
      throw new ServiceException("Error retrieving cart with ID: " + id, e);
    } catch (final Exception e) {
      log.error("An unexpected error occurred while retrieving cart with ID: {}. Reason: {}", id, e.getMessage(), e);
      throw new ServiceException("An unexpected error occurred while retrieving cart with ID: " + id, e);
    }
  }

  /**
   * Adds the specified product to the cart multiple times based on the quantity specified in the DTO.
   *
   * @param dto     The DTO containing information about the product and quantity.
   * @param cart    The cart to which the product should be added.
   * @param product The product to be added to the cart.
   */
  private void addProductsToCart(final CartItemDto dto, final Cart cart, final Product product) {
    IntStream.range(0, dto.getQuantity()).forEach(i -> cart.addProduct(product));
  }

  /**
   * Maps products from a Cart object to CartProductDto objects and sets them in a CartDto.
   *
   * @param cart    the Cart object containing products to be mapped
   * @param cartDto the CartDto object where the mapped products will be set
   */
  private void mapProductsToCartProductDto(final Cart cart, final CartDto cartDto) {
    // Map products to CartProductDto
    cartDto.setProducts(
        cart.getProducts().stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream()
            .map(entry -> CartProductDto.builder()
                .productId(entry.getKey().getId())
                .quantity(entry.getValue().intValue())
                .build())
            .toList()
    );
    cartDto.setAmount((cart.getAmount() != null && cart.getAmount() >= 0) ? cart.getAmount() : null);
  }

  /**
   * Maps a list of Cart objects to a list of CartDto objects.
   * <p>
   * This method iterates over both lists in parallel and applies the
   * {@code mapProductsToCartProductDto} method to each pair of corresponding elements.
   * The mapping operation is performed in-place on the {@code cartsDto} list.
   * <p>
   *
   * @param cartsDto the list of CartDto objects to be updated
   * @param carts    the list of Cart objects to be mapped to CartDto objects
   */
  private void mapListOfProductsToCartProductDto(final List<CartDto> cartsDto, final List<Cart> carts) {
    final Iterator<CartDto> iteratorDto = cartsDto.iterator();
    final Iterator<Cart> iterator = carts.iterator();

    while (iteratorDto.hasNext() && iterator.hasNext()) {
      mapProductsToCartProductDto(iterator.next(), iteratorDto.next());
    }
  }

  /**
   * Calculates the total amount of the cart by summing up the prices of all products in the cart.
   *
   * @param cart The cart for which the total amount is to be calculated. Must not be null and its product list must not be null.
   * @return The total amount of the cart. Returns 0.0 if the cart is empty.
   * @throws IllegalArgumentException if the cart or its product list is null.
   */
  private Double calculateCartAmount(final Cart cart) {
    // Check if the cart or the products are null
    if (cart == null || cart.getProducts() == null) {
      throw new IllegalArgumentException("Cart or products list cannot be null");
    }
    // Check if the cart is empty
    if (cart.getProducts().isEmpty()) {
      return 0.0;
    }
    // Calculate the total amount
    final var amount = cart.getProducts().stream()
        .filter(Objects::nonNull)  // Ignore null products
        .map(Product::getPrice)
        .filter(Objects::nonNull)  // Ignore products with null price
        .reduce(0.0, Double::sum);

    final DecimalFormat df = new DecimalFormat("#.##");
    df.setMaximumFractionDigits(2);
    return Double.parseDouble(df.format(amount));
  }
}
