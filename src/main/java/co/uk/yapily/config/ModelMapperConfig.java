package co.uk.yapily.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures ModelMapper for the application.
 * Adds a mapping between LeaseOfferPrettyGoodCarDealsToLeaseOffer.
 *
 * @author Samuel Catalano
 * @since 1.0.0
 */
@Configuration
public class ModelMapperConfig {

  /**
   * Configures ModelMapper for the application.
   * @return ModelMapper instance
   */
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
