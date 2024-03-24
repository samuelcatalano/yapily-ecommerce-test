package co.uk.yapily.entity;

import co.uk.yapily.repository.CartRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartTest {

  @Test
  void test_save_cart() {
    final CartRepository cartRepository = mock(CartRepository.class);

    // Create a Cart object
    final Cart cart = Cart.builder()
        .checkout(false)
        .amount(0.0)
        .products(new ArrayList<>())
        .build();

    // Set up behavior for save method of mocked repository
    when(cartRepository.save(any(Cart.class))).thenReturn(cart);

    // Save the Cart to the mocked repository
    final Cart savedCart = cartRepository.save(cart);

    // Verify that the Cart is saved correctly
    assertFalse(savedCart.isCheckout());
    assertEquals(0.0, savedCart.getAmount());
    assertNotNull(savedCart.getProducts());
    assertEquals(0, savedCart.getProducts().size());

    // Verify that save method was called once
    verify(cartRepository, times(1)).save(any(Cart.class));
  }

  @Test
  void test_update_cart() {
    final CartRepository cartRepository = mock(CartRepository.class);

    // Create a Cart object
    final Cart cart = Cart.builder()
        .id(1L)
        .checkout(false)
        .amount(0.0)
        .products(new ArrayList<>())
        .build();

    // Set up behavior for findById and save methods of mocked repository
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(cartRepository.save(any(Cart.class))).thenReturn(cart);

    // Update the Cart
    final Cart updatedCart = cartRepository.findById(1L).map(c -> {
      c.setCheckout(true);
      c.setAmount(100.0);
      return cartRepository.save(c);
    }).orElse(null);

    // Verify that the Cart is updated correctly
    assertNotNull(updatedCart);
    assertEquals(1L, updatedCart.getId());
    assertTrue(updatedCart.isCheckout());
    assertEquals(100.0, updatedCart.getAmount());

    // Verify that findById and save methods were called
    verify(cartRepository, times(1)).findById(1L);
    verify(cartRepository, times(1)).save(any(Cart.class));
  }

  @Test
  void test_delete_cart() {
    final CartRepository cartRepository = mock(CartRepository.class);

    // Set up behavior for deleteById method of mocked repository
    doNothing().when(cartRepository).deleteById(1L);

    // Delete the Cart
    cartRepository.deleteById(1L);

    // Verify that deleteById method was called once with the correct id
    verify(cartRepository, times(1)).deleteById(1L);
  }

  @Test
  void test_find_by_id() {
    final CartRepository cartRepository = mock(CartRepository.class);

    // Create a Cart object
    final Cart cart = Cart.builder()
        .id(1L)
        .checkout(false)
        .amount(0.0)
        .products(new ArrayList<>())
        .build();

    // Set up behavior for findById method of mocked repository
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

    // Find the Cart by id
    final Optional<Cart> foundCart = cartRepository.findById(1L);

    // Verify that the Cart is found correctly
    assertNotNull(foundCart);
    assertTrue(foundCart.isPresent());
    assertEquals(cart.getId(), foundCart.get().getId());
    assertEquals(cart.isCheckout(), foundCart.get().isCheckout());
    assertEquals(cart.getAmount(), foundCart.get().getAmount());
    assertEquals(cart.getProducts(), foundCart.get().getProducts());

    // Verify that findById method was called once
    verify(cartRepository, times(1)).findById(1L);
  }
}
