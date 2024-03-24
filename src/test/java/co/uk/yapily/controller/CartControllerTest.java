package co.uk.yapily.controller;

import co.uk.yapily.dto.CartDto;
import co.uk.yapily.dto.CartItemDto;
import co.uk.yapily.dto.CheckoutDto;
import co.uk.yapily.exception.ServiceException;
import co.uk.yapily.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CartService cartService;

  @Test
  @Order(1)
  void test_save_cart_ReturnsCreated() throws Exception {
    // Given
    final CartDto cartDto = new CartDto();
    final CartDto savedCartDto = new CartDto();
    when(cartService.save(cartDto)).thenReturn(savedCartDto);

    // When
    final ResultActions resultActions = mockMvc.perform(post("/carts")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(cartDto)));

    // Then
    resultActions.andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @Order(2)
  void test_save_cart_InvalidDto_ReturnsBadRequest() throws Exception {
    // Given
    final CartDto invalidCartDto = null;

    // When
    final ResultActions resultActions = mockMvc.perform(post("/carts")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidCartDto)));

    // Then
    resultActions.andExpect(status().isBadRequest());
    verifyNoInteractions(cartService);
  }

  @Test
  @Order(3)
  void test_update_cart_ValidDto_ReturnsOk() throws Exception {
    // Given
    final Long cartId = 1L;
    final CartItemDto cartItemDto = new CartItemDto();
    final CartDto updatedCartDto = new CartDto();
    when(cartService.update(cartItemDto, cartId)).thenReturn(updatedCartDto);

    // When
    final ResultActions resultActions = mockMvc.perform(put("/carts/{id}", cartId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(cartItemDto)));

    // Then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @Order(4)
  void test_update_cart_InvalidDto_ReturnsBadRequest() throws Exception {
    // Given
    final Long cartId = 1L;

    // When
    final ResultActions resultActions = mockMvc.perform(put("/carts/{id}", cartId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(null)));

    // Then
    resultActions.andExpect(status().isBadRequest());
    verifyNoInteractions(cartService);
  }

  @Test
  @Order(5)
  void test_checkout_cart_ValidId_ReturnsOk() throws Exception {
    // Given
    final Long cartId = 1L;
    final CheckoutDto checkoutDto = new CheckoutDto();
    when(cartService.checkout(cartId)).thenReturn(checkoutDto);

    // When
    final ResultActions resultActions = mockMvc.perform(post("/carts/{id}", cartId));

    // Then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @Order(6)
  void test_checkout_cart_InvalidId_ReturnsNotFound() throws Exception {
    // Given
    final Long invalidCartId = null;
    // When
    final ResultActions resultActions = mockMvc.perform(post("/carts/{id}", invalidCartId));
    // Then
    resultActions.andExpect(status().isNotFound());
    verify(cartService, never()).checkout(invalidCartId);
  }

  @Test
  @Order(7)
  void test_checkout_Cart_ReturnsNotFound() throws Exception {
    // Given
    final Long cartId = 1L;
    final CheckoutDto checkoutDto = new CheckoutDto();
    when(cartService.checkout(cartId)).thenReturn(checkoutDto);

    // When
    final ResultActions resultActions = mockMvc.perform(post("/carts/{id}/checkout", cartId));

    // Then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @Order(8)
  void test_find_cart_by_id_ValidId_ReturnsOk() throws Exception {
    // Given
    final Long cartId = 1L;
    final CartDto cartDto = new CartDto();
    when(cartService.findById(cartId)).thenReturn(cartDto);

    // When
    final ResultActions resultActions = mockMvc.perform(get("/carts/{id}", cartId));

    // Then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @Order(9)
  void test_find_Cart_by_id_InvalidId_ReturnsNotFound() throws Exception {
    // Given
    when(cartService.findById(null)).thenThrow(new ServiceException("Cart not found"));
    // When
    final ResultActions resultActions = mockMvc.perform(get("/carts/{id}", (Object) null));
    // Then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @Order(10)
  void test_find_all_carts_ReturnsOk() throws Exception {
    // Given
    final List<CartDto> cartDtos = new ArrayList<>();
    cartDtos.add(new CartDto());
    when(cartService.findAll()).thenReturn(cartDtos);

    // When
    final ResultActions resultActions = mockMvc.perform(get("/carts"));

    // Then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @Order(11)
  void test_find_all_carts_EmptyList_ReturnsOk() throws Exception {
    // Given
    final List<CartDto> emptyCartList = new ArrayList<>();
    when(cartService.findAll()).thenReturn(emptyCartList);

    // When
    final ResultActions resultActions = mockMvc.perform(get("/carts"));

    // Then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @Order(12)
  void test_find_all_carts_ServiceError_ReturnsInternalServerError() throws Exception {
    // Given
    when(cartService.findAll()).thenThrow(new ServiceException("Service error"));
    // When
    final ResultActions resultActions = mockMvc.perform(get("/carts"));
    // Then
    resultActions.andExpect(status().isInternalServerError());
  }
}
