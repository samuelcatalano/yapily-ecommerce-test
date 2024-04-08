package co.uk.yapily.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import co.uk.yapily.dto.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
