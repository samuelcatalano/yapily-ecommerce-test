package co.uk.yapily.dto;

import co.uk.yapily.dto.base.BaseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutDto extends BaseDto {

  private CartDto cart;

  @JsonProperty(value = "total_cost")
  private Double amount;
}
