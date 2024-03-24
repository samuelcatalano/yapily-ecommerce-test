package co.uk.yapily.dto;

import co.uk.yapily.dto.base.BaseDto;
import co.uk.yapily.validation.annotation.ValidLabels;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto extends BaseDto {

  @JsonProperty(value = "product_id")
  private Long id;

  @Size(max = 200, message = "The product name cannot exceed 200 characters")
  private String name;

  @NotNull(message = "The product price cannot be null")
  private Double price;

  @JsonProperty(value = "added_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date addedAt;

  @ValidLabels(message = "Restricted set of labels: [drink, food, clothes, limited]")
  @NotNull(message = "The product labels cannot be null")
  private List<String> labels = new ArrayList<>();

}
