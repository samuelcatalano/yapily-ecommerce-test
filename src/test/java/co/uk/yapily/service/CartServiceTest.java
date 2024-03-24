package co.uk.yapily.service;

import co.uk.yapily.dto.CartDto;
import co.uk.yapily.dto.CartItemDto;
import co.uk.yapily.dto.CheckoutDto;
import co.uk.yapily.dto.ProductDto;
import co.uk.yapily.entity.Cart;
import co.uk.yapily.entity.Product;
import co.uk.yapily.exception.ServiceException;
import co.uk.yapily.repository.CartRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class CartServiceTest {

  private CartService cartService;

  @Mock
  private ProductService productService;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private ModelMapper modelMapper;

  @BeforeEach
  public void setUp() {
    cartService = new CartService(cartRepository, productService, modelMapper);
  }

  @Test
  void test_save_cart_Successful() throws ServiceException {
    // Given
    final CartDto cartDto = new CartDto(); // Example cart DTO
    final Cart cartEntity = new Cart(); // Example cart entity
    final Cart savedCartEntity = new Cart(); // Example saved cart entity
    final CartDto savedCartDto = new CartDto(); // Example saved cart DTO

    when(modelMapper.map(cartDto, Cart.class)).thenReturn(cartEntity); // Mocking modelMapper behavior
    when(cartRepository.save(cartEntity)).thenReturn(savedCartEntity); // Mocking repository behavior
    when(modelMapper.map(savedCartEntity, CartDto.class)).thenReturn(savedCartDto); // Mocking modelMapper behavior

    // Act
    final CartDto result = cartService.save(cartDto);

    // Assert
    assertNotNull(result);
    assertEquals(savedCartDto, result);
  }

  @Test
  void test_update_Successful() throws ServiceException {
    // Given
    final CartItemDto cartItemDto = new CartItemDto();
    final Long cartId = 1L;
    final Cart cartEntity = new Cart();
    cartEntity.setProducts(Collections.singletonList(new Product())); // Ensure products list is initialized

    final CartDto updatedCartDto = new CartDto(); // Example updated cart DTO
    updatedCartDto.setId(cartId);

    final ProductDto productDto = new ProductDto(); // Example product DTO
    when(productService.findById(cartItemDto.getProductId())).thenReturn(productDto); // Mocking productService behavior
    when(cartRepository.findById(cartId)).thenReturn(Optional.of(cartEntity)); // Mocking repository behavior
    when(cartRepository.save(any())).thenReturn(cartEntity); // Mocking repository behavior
    when(modelMapper.map(cartEntity, CartDto.class)).thenReturn(updatedCartDto); // Mocking modelMapper behavior

    // Act
    final CartDto result = cartService.update(cartItemDto, cartId);

    // Assert
    assertNotNull(result);
    assertEquals(updatedCartDto, result);
  }

  @Test
  void test_checkout_Successful() throws ServiceException {
    // Given
    final Long cartId = 1L;
    final Cart cart = new Cart();
    cart.setId(cartId);
    cart.setCheckout(false);
    cart.addProduct(Product.builder().price(20.99).build());

    when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
    when(cartRepository.save(any(Cart.class))).thenReturn(cart);
    when(modelMapper.map(any(Cart.class), eq(CartDto.class))).thenReturn(new CartDto());

    // Act
    final CheckoutDto checkoutDto = cartService.checkout(cartId);
    // Assert
    assertNotNull(checkoutDto.getCart());
    assertTrue(checkoutDto.getAmount() >= 0);
  }

  @Test
  void test_delete_Successful() throws ServiceException {
    // Given
    final Long id = 123L; // Example ID
    // Act
    cartService.delete(id);
    // Assert
    verify(cartRepository).deleteById(id);
  }

  @Test
  void test_find_by_id_Successful() throws ServiceException {
    // Arrange
    final Long cartId = 1L;
    final Cart cart = new Cart();
    cart.setProducts(Collections.singletonList(new Product()));
    when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

    final CartDto expectedCartDto = new CartDto();
    when(modelMapper.map(cart, CartDto.class)).thenReturn(expectedCartDto);

    // Act
    final CartDto actualCartDto = cartService.findById(cartId);

    // Assert
    assertEquals(expectedCartDto, actualCartDto);
    verify(modelMapper, times(1)).map(cart, CartDto.class);
    verify(cartRepository, times(1)).findById(cartId);
  }

  @Test
  void test_find_all_Successful() throws ServiceException {
    // Given
    final List<Cart> carts = new ArrayList<>();
    final var cart1 = new Cart();
    final var cart2 = new Cart();

    cart1.setProducts(Collections.singletonList(new Product()));
    cart2.setProducts(Collections.singletonList(new Product()));
    carts.add(cart1);
    carts.add(cart2);

    when(cartRepository.findAll()).thenReturn(carts);

    final List<CartDto> expectedCartsDto = new ArrayList<>();
    expectedCartsDto.add(new CartDto());
    expectedCartsDto.add(new CartDto());

    when(modelMapper.map(carts, new TypeToken<List<CartDto>>() {
    }.getType())).thenReturn(expectedCartsDto);

    // Act
    final List<CartDto> actualCartsDto = cartService.findAll();
    // Assert
    assertEquals(expectedCartsDto.size(), actualCartsDto.size());
    verify(modelMapper, times(1)).map(carts, new TypeToken<List<CartDto>>() {
    }.getType());
    verify(cartRepository, times(1)).findAll();
  }

  @Test
  void test_save_cart_ShouldThrowServiceExceptionOnDataIntegrityViolationException() {
    // Given
    final CartDto cartDto = new CartDto(); // Example cart DTO
    when(modelMapper.map(cartDto, Cart.class)).thenReturn(new Cart()); // Mocking modelMapper behavior
    when(cartRepository.save(any())).thenThrow(DataIntegrityViolationException.class); // Mocking repository behavior

    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.save(cartDto));
  }

  @Test
  void test_save_cart_ShouldThrowServiceExceptionOnDataAccessException() {
    // Given
    final CartDto cartDto = new CartDto(); // Example cart DTO
    when(modelMapper.map(cartDto, Cart.class)).thenReturn(new Cart()); // Mocking modelMapper behavior
    when(cartRepository.save(any())).thenThrow(DataRetrievalFailureException.class); // Mocking repository behavior

    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.save(cartDto));
  }

  @Test
  void test_save_cart_ShouldThrowServiceExceptionOnUnexpectedError() {
    // Given
    final CartDto cartDto = new CartDto(); // Example cart DTO
    when(modelMapper.map(cartDto, Cart.class)).thenReturn(new Cart()); // Mocking modelMapper behavior
    when(cartRepository.save(any())).thenThrow(RuntimeException.class); // Mocking repository behavior

    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.save(cartDto));
  }

  @Test
  void test_update_cart_ShouldThrowServiceExceptionWhenCartIdIsNull() {
    // Given
    final CartItemDto cartItemDto = new CartItemDto(); // Example cart item DTO
    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.update(cartItemDto, null));
  }

  @Test
  void test_update_cart_ShouldThrowServiceExceptionWhenProductNotFound() throws ServiceException {
    // Given
    final Long cartId = 123L; // Example cart ID
    final CartItemDto cartItemDto = new CartItemDto(); // Example cart item DTO

    when(productService.findById(cartItemDto.getProductId())).thenThrow(ServiceException.class); // Mocking productService behavior
    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.update(cartItemDto, cartId));
  }

  @Test
  void test_update_cart_ShouldThrowServiceExceptionWhenCartNotFound() throws ServiceException {
    // Given
    final Long cartId = 123L; // Example cart ID
    final CartItemDto cartItemDto = new CartItemDto(); // Example cart item DTO

    when(productService.findById(cartItemDto.getProductId())).thenReturn(new ProductDto()); // Mocking productService behavior
    when(cartRepository.findById(cartId)).thenReturn(Optional.empty()); // Mocking repository behavior
    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.update(cartItemDto, cartId));
  }

  @Test
  void test_update_cart_ShouldThrowServiceExceptionOnUnexpectedError() throws ServiceException {
    // Given
    final Long cartId = 123L; // Example cart ID
    final CartItemDto cartItemDto = new CartItemDto(); // Example cart item DTO

    when(productService.findById(cartItemDto.getProductId())).thenReturn(new ProductDto()); // Mocking productService behavior
    when(cartRepository.findById(cartId)).thenReturn(Optional.of(new Cart())); // Mocking repository behavior
    when(cartRepository.save(any())).thenThrow(RuntimeException.class); // Mocking repository behavior
    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.update(cartItemDto, cartId));
  }

  @Test
  void test_checkout_ShouldThrowExceptionWhenCartIsAlreadyCheckedOut() {
    // Given
    final Long cartId = 1L;
    final Cart cart = new Cart();
    cart.setId(cartId);
    cart.setCheckout(true);

    when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
    // Act and Assert
    assertThrows(ServiceException.class, () -> cartService.checkout(cartId));
  }

  @Test
  void test_checkout_ShouldThrowExceptionWhenCartNotFound() {
    // Given
    final Long cartId = 1L;
    when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
    // Act and Assert
    assertThrows(ServiceException.class, () -> cartService.checkout(cartId));
  }

  @Test
  void teat_checkout_ShouldThrowExceptionWhenCartIsAlreadyCheckedOut() {
    // Given
    final Long cartId = 1L;
    final Cart cart = new Cart();
    cart.setId(cartId);
    cart.setCheckout(true); // Set cart to already checked out

    when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
    // Act and Assert
    final ServiceException exception = assertThrows(ServiceException.class, () -> cartService.checkout(cartId));
    assertEquals("An unexpected error occurred while checking out cart: Reason: Cart is already checked out!", exception.getMessage());
  }

  @Test
  void test_delete_ShouldLogWarningWhenDeletingNonExistentCart() {
    // Arrange
    final Long id = 456L; // Example non-existent ID
    doThrow(EmptyResultDataAccessException.class).when(cartRepository).deleteById(id); // Mocking repository behavior
    // Act
    try {
      cartService.delete(id);
    } catch (final ServiceException e) {
      // Ignoring ServiceException as it's expected in this scenario
    }
    // Assert
    verify(cartRepository).deleteById(id);
  }

  @Test
  void test_find_all_ShouldThrowServiceException_whenDataRetrievalFailureExceptionOccurs() {
    // Arrange
    when(cartRepository.findAll()).thenThrow(DataRetrievalFailureException.class);

    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.findAll());
    verify(cartRepository, times(1)).findAll();
  }

  @Test
  void test_find_by_id_ShouldThrowServiceException_whenCartDoesNotExist() {
    // Arrange
    final Long cartId = 1L;
    when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ServiceException.class, () -> cartService.findById(cartId));
    verify(cartRepository, times(1)).findById(cartId);
  }
}
