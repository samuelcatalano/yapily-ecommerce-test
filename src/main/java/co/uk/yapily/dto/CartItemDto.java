package co.uk.yapily.dto;

import co.uk.yapily.dto.base.BaseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto extends BaseDto {

  @JsonProperty(value = "product_id")
  private Long productId;
  private int quantity;

}
