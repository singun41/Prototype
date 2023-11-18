// package com.studykeycloak.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpHeaders;

// import io.swagger.v3.oas.models.Components;
// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Info;
// import io.swagger.v3.oas.models.security.SecurityRequirement;
// import io.swagger.v3.oas.models.security.SecurityScheme;

// @Configuration
// public class ConfigSwagger {
//   @Bean
//   OpenAPI openApi() {
//     return new OpenAPI()
//     .components(
//       new Components()
//       .addSecuritySchemes(
//         "JWT",
//         new SecurityScheme()
//         .type(SecurityScheme.Type.HTTP)
//         .scheme("bearer")
//         .bearerFormat("JWT")
//         .in(SecurityScheme.In.HEADER)
//         .name(HttpHeaders.AUTHORIZATION)
//       )
//     ).addSecurityItem(
//       new SecurityRequirement().addList("JWT")
//     ).info(
//       new Info()
//       .title("SpringDoc-Swagger-Gateway")
//       .description("SpringDoc - Swagger test")
//       .version("0.0.1")
//     );
//   }
// }
