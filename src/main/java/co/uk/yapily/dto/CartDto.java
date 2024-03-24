package co.uk.yapily.dto;

import co.uk.yapily.dto.base.BaseDto;
import co.uk.yapily.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto extends BaseDto {

  @JsonProperty(value = "cart_id")
  private Long id;

  @JsonProperty(value = "check_out")
  private boolean checkout;

  private List<CartProductDto> products;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "total_cost")
  private Double amount;

}
