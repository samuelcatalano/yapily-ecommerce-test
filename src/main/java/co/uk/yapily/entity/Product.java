package co.uk.yapily.entity;

import co.uk.yapily.converter.StringListConverter;
import co.uk.yapily.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

  @Column(name = "name", unique = true)
  private String name;

  @Column(name = "price")
  private Double price;

  @Column(name = "added_at")
  private Date addedAt = Date.from(Instant.now());

  @Convert(converter = StringListConverter.class)
  @Column(name = "labels", nullable = false, columnDefinition = "TEXT")
  private List<String> labels = new ArrayList<>();

  @ManyToMany(mappedBy = "products")
  @JsonBackReference // Prevents infinite recursion
  private List<Cart> carts = new ArrayList<>();

  @PrePersist
  public void persistDate() {
    this.addedAt = Date.from(Instant.now());
  }
}