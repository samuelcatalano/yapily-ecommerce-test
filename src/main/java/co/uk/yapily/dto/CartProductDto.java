package co.uk.yapily.dto;

import co.uk.yapily.dto.base.BaseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartProductDto extends BaseDto {

  @JsonProperty("product_id")
  private Long productId;
  private int quantity;
}
