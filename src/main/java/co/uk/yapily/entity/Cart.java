package co.uk.yapily.entity;

import co.uk.yapily.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cart")
public class Cart extends BaseEntity {

  @ManyToMany
  @JoinTable(name = "cart_product",
      joinColumns = @JoinColumn(name = "cart_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id"))
  @JsonManagedReference // Prevents infinite recursion
  private List<Product> products;

  @Column(name = "check_out")
  private boolean checkout = false;

  @Column(name = "total_cost")
  private Double amount;

  /**
   * Adds a product to the cart.
   * @param product the product to add
   */
  public void addProduct(final Product product) {
    if (products == null) {
      products = new ArrayList<>();
    }
    products.add(product);
  }
}
