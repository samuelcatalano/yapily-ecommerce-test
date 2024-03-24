package co.uk.yapily.dto;

import co.uk.yapily.dto.base.BaseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponseDto extends BaseDto {

  @JsonProperty(value = "cart_id")
  private Long id;

  private List<CartProductDto> products;

  @JsonProperty(value = "check_out")
  private boolean checkout;
}
