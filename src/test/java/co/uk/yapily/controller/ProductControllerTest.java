package co.uk.yapily.controller;

import co.uk.yapily.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @Order(1)
  void test_save_product_EndpointTest() throws Exception {
    final ProductDto productDto = ProductDto.builder()
         .id(1L)
         .name("Test Product")
         .price(20.99)
         .labels(List.of("limited", "drink"))
         .build();

    final String jsonBody = objectMapper.writeValueAsString(productDto);
    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void teat_find_product_by_id_EndpointTest() throws Exception {
    final Long productId = 1L;
    mockMvc.perform(get("/products/{id}", productId))
           .andExpect(status().isOk());
  }

  @Test
  @Order(3)
  void test_find_all_products_EndpointTest() throws Exception {
    mockMvc.perform(get("/products"))
           .andExpect(status().isOk());
  }

  @Test
  @Order(4)
  void deleteProduct_EndpointTest() throws Exception {
    final Long productId = 1L;

    mockMvc.perform(delete("/products/{id}", productId))
           .andExpect(status().isNoContent());
  }
}
