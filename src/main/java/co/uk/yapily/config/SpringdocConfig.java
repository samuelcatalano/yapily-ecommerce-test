package co.uk.yapily.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides the configuration for the SpringDoc library.
 *
 * @author Samuel Catalano
 * @since 1.0.0
 */
@Configuration
public class SpringdocConfig {

  @Bean
  public OpenAPI springDocOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("e-commerce API")
            .description("Exposing e-commerce REST methods")
            .version("1.0.0")
            .license(new License()
                .name("Apache 2.0")
                .url("https://springdoc.org")))
        .externalDocs(new ExternalDocumentation()
            .description("Yapily | e-commerce API Exercise")
            .url("https://www.yapily.com/"));
  }
}
